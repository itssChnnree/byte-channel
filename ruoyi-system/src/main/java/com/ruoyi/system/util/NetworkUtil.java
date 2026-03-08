package com.ruoyi.system.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/26
 */
@Slf4j
public class NetworkUtil {


    /**
     * 检查指定 IP 的网络连通性
     * @param ipAddress 要检查的 IP 地址
     * @return true 如果主机在线，false 如果主机离线
     */
    public static boolean checkConnectivity(String ipAddress) {
        try {
            // 构建命令
            String[] command = {
                    "/bin/bash",
                    "-c",
                    "nmap -sn " + ipAddress + " | grep -q \"Host is up\" && echo \"true\" || echo \"false\""
            };

            // 执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 读取命令输出
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if ("true".equals(line.trim())) {
                    return true;
                }
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();

            // 如果命令执行失败，记录错误
            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                StringBuilder errorMessage = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                LogEsUtil.warn("命令执行错误: " + errorMessage);
            }

        } catch (IOException | InterruptedException e) {
            LogEsUtil.error("执行命令异常"+e.getMessage(),e);
            return false;
        }

        return false;
    }
}
