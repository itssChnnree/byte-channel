package com.ruoyi.system.proxyServerTestingStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/23
 */
public class VlessRealityStrategy {
    // 代理服务器列表
    private static final List<ProxyServer> PROXY_SERVERS = Arrays.asList(
            new ProxyServer("proxy1", "proxy1.example.com", 1081),
            new ProxyServer("proxy2", "proxy2.example.com", 1082),
            // ... 添加所有10个代理服务器
            new ProxyServer("proxy10", "proxy10.example.com", 1090)
    );

    // Google 测试 URL
    private static final String TEST_URL = "https://www.google.com/generate_204";

    // 报告文件路径
    private static final String REPORT_FILE = "proxy_failures.csv";

    public static void main(String[] args) {
        // 启动定时任务
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                VlessRealityStrategy::checkAllProxies,
                0,  // 立即开始
                1,  // 每小时执行一次
                TimeUnit.HOURS
        );
    }

    private static void checkAllProxies() {
        System.out.println("[" + LocalDateTime.now() + "] 开始代理服务器健康检查...");

        List<String> failedProxies = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(PROXY_SERVERS.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (ProxyServer proxy : PROXY_SERVERS) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    if (!testProxy(proxy)) {
                        failedProxies.add(proxy.getAddress());
                        System.err.println("代理失败: " + proxy.getName() + " (" + proxy.getAddress() + ")");
                    } else {
                        successCount.incrementAndGet();
                        System.out.println("代理成功: " + proxy.getName());
                    }
                } catch (Exception e) {
                    System.err.println("测试异常: " + proxy.getName() + " - " + e.getMessage());
                    failedProxies.add(proxy.getAddress());
                }
            }, executor));
        }

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // 生成报告
        generateReport(failedProxies, successCount.get());

        System.out.println("检查完成。成功: " + successCount.get() +
                ", 失败: " + failedProxies.size());
    }

    private static boolean testProxy(ProxyServer proxy) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", proxy.getLocalPort())))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TEST_URL))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Google 204 页面返回空内容
            return response.statusCode() == 204 && response.body().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static void generateReport(List<String> failedProxies, int successCount) {
        try (FileWriter writer = new FileWriter(REPORT_FILE, true)) {
            // 写入表头（如果文件为空）
            if (new File(REPORT_FILE).length() == 0) {
                writer.write("Timestamp,SuccessCount,FailedCount,FailedProxies\n");
            }

            // 写入数据行
            writer.write(String.format("%s,%d,%d,%s\n",
                    LocalDateTime.now(),
                    successCount,
                    failedProxies.size(),
                    String.join(";", failedProxies)
            ));

            System.out.println("报告已保存至: " + REPORT_FILE);
        } catch (IOException e) {
            System.err.println("报告保存失败: " + e.getMessage());
        }

        // 发送警报（如果有失败）
        if (!failedProxies.isEmpty()) {
            sendAlert(failedProxies);
        }
    }

    private static void sendAlert(List<String> failedProxies) {
        // 实现实际的警报发送逻辑（邮件、Slack等）
        System.err.println("警报: 以下代理无法访问 Google:");
        failedProxies.forEach(System.err::println);

        // 示例：发送邮件
        // EmailService.sendAlert("代理故障警报", "故障代理: " + String.join(", ", failedProxies));
    }

    static class ProxyServer {
        private final String name;
        private final String address;
        private final int localPort;

        public ProxyServer(String name, String address, int localPort) {
            this.name = name;
            this.address = address;
            this.localPort = localPort;
        }

        public String getName() { return name; }
        public String getAddress() { return address; }
        public int getLocalPort() { return localPort; }
    }
}
