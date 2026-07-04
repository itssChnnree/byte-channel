package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.dto.SelfDeploySocks5Dto;
import com.ruoyi.system.http.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * 自助部署SOCKS5节点控制器
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-07-04
 */
@Api(tags = "自助部署SOCKS5节点")
@RestController
@RequestMapping("/selfDeploySocks5")
public class SelfDeploySocks5Controller {

    @Resource
    private RedisCache redisCache;

    private static final String SCRIPT_URL = "https://raw.githubusercontent.com/itssChnnree/byte-channel/master/ruoyi-system/src/main/resources/go/threeGoSh/startSocket5.sh";

    private static final Random RANDOM = new Random();

    @PostMapping("/execute")
    @ApiOperation("自助部署SOCKS5节点")
    public Result execute(@RequestBody @Valid SelfDeploySocks5Dto dto, BindingResult bindingResult) {
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

        int socks5Port;
        if (dto.getXrayPort() != null && dto.getXrayPort() >= 10000 && dto.getXrayPort() <= 65535) {
            socks5Port = dto.getXrayPort();
        } else {
            socks5Port = 10000 + RANDOM.nextInt(55536);
        }

        String command = buildCommand(socks5Port, dto.getSocks5User(), dto.getSocks5Pass());

        return executeSsh(ip, sshPort, username, sshPassword, command);
    }

    private String buildCommand(int port, String socks5User, String socks5Pass) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("curl -fsSL -o /tmp/startSocket5.sh ").append(SCRIPT_URL)
           .append(" && bash /tmp/startSocket5.sh -p ").append(port);
        if (StrUtil.isNotBlank(socks5User)) {
            cmd.append(" -u '").append(socks5User.trim()).append("'");
        }
        if (StrUtil.isNotBlank(socks5Pass)) {
            cmd.append(" -pw '").append(socks5Pass.trim()).append("'");
        }
        cmd.append(" && rm -f /tmp/startSocket5.sh");
        return cmd.toString();
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
            if (password == null || password.isEmpty()) {
                return Result.fail("搭建失败，请联系管理员");
            }

            return Result.success(password, password);
        } catch (Exception e) {
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
        // 匹配 /query-config/socks5/密码 格式
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("/query-config/socks5/([a-zA-Z0-9]{10})");
        java.util.regex.Matcher m = p.matcher(output);
        if (m.find()) {
            return m.group(1);
        }

        // 兜底：匹配 /query-config/socks5/ 后跟非空白字符
        String[] lines = output.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.contains("/query-config/socks5/")) {
                int idx = line.indexOf("/query-config/socks5/");
                if (idx >= 0) {
                    String sub = line.substring(idx + "/query-config/socks5/".length()).trim();
                    return sub.replaceAll("[^a-zA-Z0-9]", "");
                }
            }
        }

        // 再兜底：匹配 /query-config/ 格式（兼容）
        p = java.util.regex.Pattern.compile("/query-config/([a-zA-Z0-9]{10})");
        m = p.matcher(output);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }
}
