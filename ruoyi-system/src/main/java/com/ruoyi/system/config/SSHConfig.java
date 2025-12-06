package com.ruoyi.system.config;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/11/27
 */
public class SSHConfig {

    // SSH会话对象
    private Session session;
    // Shell通道对象
    private ChannelShell channel;
    // 从服务器读取数据的输入流
    private InputStream inputFromServer;
    // 向服务器发送数据的输出流
    private OutputStream outputToServer;
}
