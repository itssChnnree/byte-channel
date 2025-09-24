package com.ruoyi.system.domain.entity.XrayOutbound;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundConfig {
    private String tag;
    private String protocol;
    private Settings settings;
    private StreamSettings streamSettings;
    private Mux mux;



    public static OutboundConfig buildOutboundConfig(String userId,String ip,int port,String tags,
                                                String serverName,String publicKey,String shortId){
        // 创建用户
        User user = new User();
        user.setId(userId);
        user.setAlterId(0);
        user.setEmail("t@t.tt");
        user.setSecurity("auto");
        user.setEncryption("none");
        user.setFlow("xtls-rprx-vision");

        // 创建 VNext
        VNext vnext = new VNext();
        vnext.setAddress(ip);
        vnext.setPort(port);
        vnext.setUsers(List.of(user));

        // 创建 Settings
        Settings settings = new Settings();
        settings.setVnext(List.of(vnext));

        List<String> split = StrUtil.split(serverName, ",");
        // 创建 RealitySettings
        RealitySettings realitySettings = new RealitySettings();
        realitySettings.setServerName(split.get(0));
        realitySettings.setFingerprint("chrome");
        realitySettings.setShow(false);
        realitySettings.setPublicKey(publicKey);
        realitySettings.setShortId(shortId);
        realitySettings.setSpiderX("/");

        // 创建 StreamSettings
        StreamSettings streamSettings = new StreamSettings();
        streamSettings.setNetwork("tcp");
        streamSettings.setSecurity("reality");
        streamSettings.setRealitySettings(realitySettings);

        // 创建 Mux
        Mux mux = new Mux();
        mux.setEnabled(false);
        mux.setConcurrency(-1);

        // 创建最终配置
        OutboundConfig config = new OutboundConfig();
        config.setTag(tags);
        config.setProtocol("vless");
        config.setSettings(settings);
        config.setStreamSettings(streamSettings);
        config.setMux(mux);
        return config;
    }
}
