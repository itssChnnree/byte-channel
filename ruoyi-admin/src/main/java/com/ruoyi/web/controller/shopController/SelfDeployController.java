package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.dto.SelfDeployDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

@Api(tags = "自助部署节点")
@RestController
@RequestMapping("/selfDeploy")
public class SelfDeployController {

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private RedisCache redisCache;

    private static final String SCRIPT_IN_URL = "https://raw.githubusercontent.com/itssChnnree/byte-channel/master/ruoyi-system/src/main/resources/go/threeGoSh/xrayInStart.sh";

    private static final String SCRIPT_RELAY_URL = "https://raw.githubusercontent.com/itssChnnree/byte-channel/master/ruoyi-system/src/main/resources/go/threeGoSh/xrayRelay.sh";

    private static final Random RANDOM = new Random();

    @PostMapping("/execute")
    @ApiOperation("自助部署Xray节点（入口/中转自适应）")
    public Result execute(@RequestBody @Valid SelfDeployDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + dto.getUuid();
        String cachedCode = redisCache.getCacheObject(verifyKey);
        if (cachedCode == null) {
            return Result.fail("验证码已过期，请刷新后重试");
        }
        if (!cachedCode.equalsIgnoreCase(dto.getCode().trim())) {
            return Result.fail("验证码错误");
        }
        redisCache.deleteObject(verifyKey);

        String ip = dto.getIp().trim();
        String sshPort = (dto.getPort() != null && !dto.getPort().trim().isEmpty()) ? dto.getPort().trim() : "22";
        String username = dto.getUsername().trim();
        String sshPassword = dto.getPassword();

        int xrayPort;
        if (dto.getXrayPort() != null && dto.getXrayPort() >= 10000 && dto.getXrayPort() <= 65535) {
            xrayPort = dto.getXrayPort();
        } else {
            xrayPort = 10000 + RANDOM.nextInt(55536);
        }

        String command;
        if (StrUtil.isNotBlank(dto.getNextPassword())) {
            UpstreamInfo upstream = queryUpstream(dto.getNextPassword());
            if (upstream == null) {
                return Result.fail("查询上游节点配置失败，请检查密码是否正确");
            }
            String mode = (dto.getNextType() != null && dto.getNextType() == 1) ? "full" : "incremental";
            command = buildRelayCommand(xrayPort, upstream, mode);
        } else {
            command = buildInboundCommand(xrayPort);
        }

        return executeSsh(ip, sshPort, username, sshPassword, command);
    }

    private String buildInboundCommand(int port) {
        return "curl -fsSL -o /tmp/xrayInStart.sh " + SCRIPT_IN_URL
                + " && bash /tmp/xrayInStart.sh -p " + port
                + " -d 'lacity.gov:443' -s 'lacity.gov,www.lacity.gov'"
                + " && rm -f /tmp/xrayInStart.sh";
    }

    private String buildRelayCommand(int port, UpstreamInfo upstream, String mode) {
        String[] sniArray = upstream.sni.split(",");
        return "curl -fsSL -o /tmp/xrayRelay.sh " + SCRIPT_RELAY_URL
                + " && bash /tmp/xrayRelay.sh -p " + port
                + " -d 'lacity.gov:443' -s 'lacity.gov'"
                + " -u " + upstream.address
                + " -r " + upstream.port
                + " -n '" + upstream.uuid + "'"
                + " -k '" + upstream.publicKey + "'"
                + " -t " + upstream.shortId
                + " -w " + sniArray[0]
                + " -m '" + mode + "'"
                + " && rm -f /tmp/xrayRelay.sh";
    }

    private UpstreamInfo queryUpstream(String password) {
        ServerResources sr = serverResourcesMapper.selectByPassword(password.trim());
        if (sr == null) {
            return null;
        }
        UpstreamInfo info = new UpstreamInfo();
        info.uuid = sr.getUserId();
        info.address = sr.getResourcesIp();
        info.port = sr.getNodePort();
        info.publicKey = sr.getPublicBrokerKey();
        info.shortId = sr.getShortId();
        info.sni = sr.getSni();
        if (StrUtil.isBlank(info.sni)) {
            info.sni = "lacity.gov";
        }
        if (StrUtil.isBlank(info.uuid) || StrUtil.isBlank(info.address)
                || StrUtil.isBlank(info.port) || StrUtil.isBlank(info.publicKey)
                || StrUtil.isBlank(info.shortId)) {
            return null;
        }
        return info;
    }

    private Result executeSsh(String ip, String sshPort, String username, String sshPassword, String command) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;

        try {
            session = jsch.getSession(username, ip, Integer.parseInt(sshPort));
            session.setPassword(sshPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "password");
            session.connect(15000);

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setPty(false);

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(channel.getErrStream()));

            channel.connect(120000);

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            StringBuilder errOutput = new StringBuilder();
            while ((line = errReader.readLine()) != null) {
                errOutput.append(line).append("\n");
            }

            int exitCode = channel.getExitStatus();
            String fullOutput = output.toString();

            if (exitCode != 0 && !errOutput.toString().isEmpty()) {
                return Result.fail("搭建失败，请联系管理员");
            }

            String password = extractPassword(fullOutput);
            if (StrUtil.isNotEmpty(password)) {
                password = password.substring(0, Math.min(password.length(), 10));
            }

            if (password == null || password.isEmpty()) {
                return Result.fail("搭建失败，请联系管理员");
            }

            return Result.success(password, password);
        } catch (com.jcraft.jsch.JSchException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("timeout")) {
                return Result.fail("SSH连接超时，请检查IP和端口是否正确");
            }
            if (msg != null && msg.toLowerCase().contains("auth")) {
                return Result.fail("SSH认证失败，用户名或密码错误");
            }
            if (msg != null && msg.toLowerCase().contains("connection refused")) {
                return Result.fail("SSH连接被拒绝，端口未开放或SSH服务未启动");
            }
            return Result.fail("SSH连接失败: " + (msg != null ? msg : "未知错误"));
        } catch (Exception e) {
            return Result.fail("搭建失败，请联系管理员");
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private String extractPassword(String output) {
        String[] lines = output.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.contains("/query-config/")) {
                int idx = line.indexOf("/query-config/");
                if (idx >= 0) {
                    String sub = line.substring(idx + "/query-config/".length()).trim();
                    return sub.replaceAll("[^a-zA-Z0-9]", "");
                }
            }
        }

        java.util.regex.Pattern p = java.util.regex.Pattern.compile("/query-config/([a-zA-Z0-9]{10})");
        java.util.regex.Matcher m = p.matcher(output);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static class UpstreamInfo {
        String uuid;
        String address;
        String port;
        String publicKey;
        String shortId;
        String sni;
    }
}
