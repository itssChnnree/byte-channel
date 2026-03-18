package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.jcraft.jsch.*;
import com.ruoyi.system.domain.dto.SshTemporaryDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.service.ISshService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ServerEndpoint(value = "/ssh")
public class SshWebSocketHandler {


    private static ServerResourcesMapper serverResourcesMapper;


    private static ISshService sshService;

    @PostConstruct
    public void init() {
        serverResourcesMapper = SpringUtil.getBean(ServerResourcesMapper.class);
        sshService = SpringUtil.getBean(ISshService.class);
    }


    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // 自定义调度线程池，用于 Caffeine 过期检查
    private static final java.util.concurrent.ScheduledExecutorService scheduledExecutor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "caffeine-expiration-scheduler");
                t.setDaemon(true);
                return t;
            });

    /**
     * WebSocket Session 缓存 - 1分钟无访问过期
     * 作为主导生命周期，过期时自动清理关联的 SSH 连接
     */
    private static final Cache<String, WebSocketSessionHolder> wsSessions = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .maximumSize(100)
            // 使用自定义执行器
            .executor(scheduledExecutor)
            .removalListener((String key, WebSocketSessionHolder holder, RemovalCause cause) -> {
                if (holder != null) {
                    LogEsUtil.info("WebSocket 过期: " + key + ", 过期原因: " + cause);
                    // WebSocket 过期时，关闭关联的 SSH 连接
                    if (holder.sshConnection != null) {
                        holder.sshConnection.close();
                    }
                    // 通知前端连接已断开
                    notifyDisconnected(holder.session, cause);
                }
            })
            .build();

    // 启动定期清理任务，每 10 秒检查一次过期条目
    static {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            wsSessions.cleanUp();
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 通知前端连接已断开
     */
    private static void notifyDisconnected(javax.websocket.Session wsSession, RemovalCause cause) {
        if (wsSession != null && wsSession.isOpen()) {
            try {
                String reason = cause == RemovalCause.EXPIRED ? "连接超时（15分钟无活动）" :
                        cause == RemovalCause.SIZE ? "连接数超限" : "连接已关闭";
                Map<String, String> map = new HashMap<>();
                map.put("type", "disconnected");
                map.put("data", reason);
                map.put("reconnect", "true");  // 提示前端可以重新连接
                String json = objectMapper.writeValueAsString(map);
                wsSession.getBasicRemote().sendText(json);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @OnOpen
    public void onOpen(javax.websocket.Session session) {
        // 从 URL 查询参数中获取 token 进行权限校验
        // 前端连接时需携带：ws://sshTemporaryDto.getIp()/ssh?token=Bearer+xxxxx
        String token = null;
        java.util.List<String> tokenList = session.getRequestParameterMap().get("token");
        if (tokenList != null && !tokenList.isEmpty()) {
            token = tokenList.get(0);
        }
        if (!validateToken(token)) {
            try {
                sendError(session, "权限校验失败，请重新登录");
                session.close(new javax.websocket.CloseReason(
                        javax.websocket.CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
            } catch (Exception e) {
                // ignore
            }
            return;
        }
        wsSessions.put(session.getId(), new WebSocketSessionHolder(session,token));
        LogEsUtil.info("WebSocket 连接: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, javax.websocket.Session session) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String type = json.get("type").asText();
            switch (type) {
                case "connect":
                    handleConnect(session, json);
                    break;
                case "input":
                    handleInput(session, json);
                    break;
                case "resize":
                    handleResize(session, json);
                    break;
                default:
                    sendError(session, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            sendError(session, "Error processing message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(javax.websocket.Session session) {
        String sessionId = session.getId();
        // invalidate 会触发 RemovalListener，自动关闭 SSH
        wsSessions.invalidate(sessionId);
        LogEsUtil.info("WebSocket closed: " + sessionId);
    }

    @OnError
    public void onError(javax.websocket.Session session, Throwable error) {
        String sessionId = session.getId();
        LogEsUtil.info("WebSocket error for session " + sessionId + ": " + error.getMessage());
        wsSessions.invalidate(sessionId);
    }

    private void handleConnect(javax.websocket.Session session, JsonNode json) {
        SshTemporaryDto sshTemporaryDto = buildSshTemporaryDto(json,session.getId());
        LogEsUtil.info("WebSocket connect: " + sshTemporaryDto.getIp() + ":" + sshTemporaryDto.getPort() + ", username: " + sshTemporaryDto.getUsername());
        executor.submit(() -> {
            try {
                JSch jsch = new JSch();
                com.jcraft.jsch.Session sshSession = jsch.getSession(sshTemporaryDto.getUsername(), sshTemporaryDto.getIp(), Integer.parseInt(sshTemporaryDto.getPort()));
                sshSession.setPassword(sshTemporaryDto.getPassword());
                // 跳过主机密钥检查（生产环境应该改进）
                sshSession.setConfig("StrictHostKeyChecking", "no");
                sshSession.connect(10000);
                Channel channel = sshSession.openChannel("shell");
                ((ChannelShell) channel).setPtyType("xterm-256color");
                ((ChannelShell) channel).setPtySize(120, 30, 0, 0);
                InputStream inputStream = channel.getInputStream();
                OutputStream outputStream = channel.getOutputStream();
                channel.connect();

                SshConnection conn = new SshConnection(sshSession, channel, inputStream, outputStream);

                // 将 SSH 连接关联到 WebSocket Session Holder
                WebSocketSessionHolder holder = wsSessions.getIfPresent(session.getId());
                if (holder != null) {
                    holder.setSshConnection(conn);
                    CompletableFuture.runAsync(()
                            -> sshService.addTemporarySsh(sshTemporaryDto,sshService.getToken(holder.token)));

                }
                sendMessage(session, "connected", "SSH connection established");

                // 读取 SSH 输出并发送到前端
                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    if (!session.isOpen()) break;
                    String output = new String(buffer, 0, len, StandardCharsets.UTF_8);
                    sendOutput(session, output);
                }
            } catch (com.jcraft.jsch.JSchException e) {
                String msg = e.getMessage();
                if (msg != null && msg.toLowerCase().contains("timeout")) {
                    sendError(session, "连接超时：无法连接到 " + sshTemporaryDto.getIp() + ":" + sshTemporaryDto.getPort() + "，请检查IP/端口是否正确或服务器是否可达");
                } else if (msg != null && msg.toLowerCase().contains("auth")) {
                    sendError(session, "认证失败：用户名或密码错误");
                } else if (msg != null && msg.toLowerCase().contains("connection refused")) {
                    sendError(session, "连接被拒绝：" + sshTemporaryDto.getIp() + ":" + sshTemporaryDto.getPort() + " 端口未开放或SSH服务未启动");
                } else {
                    sendError(session, "SSH连接失败：" + (msg != null ? msg : "未知错误"));
                }
            } catch (Exception e) {
                sendError(session, "Connection failed: " + e.getMessage());
            }
        });
    }

    private SshTemporaryDto buildSshTemporaryDto(JsonNode json,String sessionId){
        SshTemporaryDto sshTemporaryDto = new SshTemporaryDto();
        if (json == null || sessionId == null){
            return sshTemporaryDto;
        }
        String connectType = json.get("connectType").asText();
        if ("temporary".equals(connectType)){
            // 判断是否有 username 字段且不为空
            JsonNode usernameNode = json.get("username");
            if (usernameNode != null && !usernameNode.isNull() && StrUtil.isNotBlank(usernameNode.asText())){
                String host = json.get("host").asText();
                String port = json.get("port").asText();
                String password = json.get("password").asText();
                sshTemporaryDto.setUsername(usernameNode.asText());
                sshTemporaryDto.setIp(host);
                sshTemporaryDto.setPort(port);
                sshTemporaryDto.setPassword(password);
                return sshTemporaryDto;
            }else{
                // 历史记录连接：从 token 和 host 查询
                WebSocketSessionHolder holder = wsSessions.getIfPresent(sessionId);
                if (holder != null){
                    String token = holder.token;
                    String host = json.get("host").asText();
                    if (StrUtil.isNotBlank(host)){
                        return sshService.getByToken(token, host);
                    }
                }
            }
        }else if ("resources".equals(connectType)){
            JsonNode resourceIdNode = json.get("resourceId");
            if (resourceIdNode != null && !resourceIdNode.isNull() && StrUtil.isNotBlank(resourceIdNode.asText())){
                String resourceId = resourceIdNode.asText();
                ServerResources serverResources = serverResourcesMapper.selectById(resourceId);
                sshTemporaryDto.setIp(serverResources.getResourcesIp());
                sshTemporaryDto.setPort(serverResources.getResourcesPort());
                sshTemporaryDto.setUsername(serverResources.getResourcesUserName());
                sshTemporaryDto.setPassword(serverResources.getResourcesPassword());
                return sshTemporaryDto;
            }
        }else {
            return null;
        }
        return null;
    }

    private void handleInput(javax.websocket.Session session, JsonNode json) {
        String input = json.get("data").asText();
        // 获取当前连接的SSH服务器IP
        WebSocketSessionHolder holder = wsSessions.getIfPresent(session.getId());
        String serverIp = "unknown";
        if (holder != null && holder.sshConnection != null && holder.sshConnection.session != null) {
            serverIp = holder.sshConnection.session.getHost();
        }
        LogEsUtil.info("WebSocket input from [" + serverIp + "]: " + input);
        if (holder != null && holder.sshConnection != null) {
            try {
                holder.sshConnection.outputStream.write(input.getBytes(StandardCharsets.UTF_8));
                holder.sshConnection.outputStream.flush();
            } catch (IOException e) {
                sendError(session, "Failed to send input: " + e.getMessage());
            }
        }
    }

    private void handleResize(javax.websocket.Session session, JsonNode json) {
        int cols = json.get("cols").asInt();
        int rows = json.get("rows").asInt();
        WebSocketSessionHolder holder = wsSessions.getIfPresent(session.getId());
        if (holder != null && holder.sshConnection != null && holder.sshConnection.channel instanceof ChannelShell) {
            ((ChannelShell) holder.sshConnection.channel).setPtySize(cols, rows, 0, 0);
        }
    }

    private void sendMessage(javax.websocket.Session session, String type, String data) {
        try {
            if (session.isOpen()) {
                Map<String, String> map = new HashMap<>();
                map.put("type", type);
                map.put("data", data);
                String json = objectMapper.writeValueAsString(map);
                session.getBasicRemote().sendText(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendOutput(javax.websocket.Session session, String output) {
        sendMessage(session, "output", output);
    }

    private void sendError(javax.websocket.Session session, String error) {
        sendMessage(session, "error", error);
    }

    /**
     * WebSocket Session 包装器，持有 SSH 连接
     */
    private static class WebSocketSessionHolder {
        final javax.websocket.Session session;
        SshConnection sshConnection;
        String token;

        WebSocketSessionHolder(javax.websocket.Session session,String token) {
            this.token = token;
            this.session = session;
        }

        void setSshConnection(SshConnection conn) {
            this.sshConnection = conn;
        }
    }

    /**
     * Token 权限校验预留方法
     * 前端在 WebSocket URL 中携带：?token=Bearer+xxxxx
     * @param token URL 参数中的 token 值
     * @return true 校验通过，false 校验失败
     */
    private boolean validateToken(String token) {
        // TODO: 接入实际权限校验逻辑，例如调用 TokenService 验证 token 有效性
        return true;
    }

    private static class SshConnection {
        com.jcraft.jsch.Session session;
        Channel channel;
        InputStream inputStream;
        OutputStream outputStream;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        SshConnection(com.jcraft.jsch.Session session, Channel channel, InputStream in, OutputStream out) {
            this.session = session;
            this.channel = channel;
            this.inputStream = in;
            this.outputStream = out;
        }

        /**
         * 标记为已关闭，返回是否是首次关闭
         * 用于防止重复关闭
         */
        boolean markClosed() {
            return closed.compareAndSet(false, true);
        }

        boolean isClosed() {
            return closed.get();
        }

        void close() {
            if (!markClosed()) return;  // 已关闭则跳过
            try {
                LogEsUtil.info("Closing SSH connection...");
                if (channel != null) channel.disconnect();
                if (session != null) session.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}