package com.ruoyi.system.Util;

import java.io.File;
import java.io.IOException;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/23
 */
public class XrayManager {
    private static Process xrayProcess;
    private static final String XRAY_CONFIG = "multi-proxy.json";
    private static final String XRAY_BINARY = "./xray";

    public static void startXray() throws IOException {
        if (xrayProcess != null && xrayProcess.isAlive()) {
            System.out.println("Xray 已在运行");
            return;
        }

        ProcessBuilder builder = new ProcessBuilder(
                XRAY_BINARY, "run", "-c", XRAY_CONFIG
        );

        builder.directory(new File("."));
        builder.redirectErrorStream(true);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        xrayProcess = builder.start();

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (xrayProcess != null) {
                xrayProcess.destroy();
            }
        }));

        System.out.println("Xray 已启动");
    }

    public static void stopXray() {
        if (xrayProcess != null && xrayProcess.isAlive()) {
            xrayProcess.destroy();
            System.out.println("Xray 已停止");
        }
    }

    public static void restartXray() throws IOException {
        stopXray();
        startXray();
    }
}
