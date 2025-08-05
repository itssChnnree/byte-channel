package com.ruoyi.system.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author a1152
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XrayConfig {
    private LogConfig log;
    private List<Inbound> inbounds;
    private List<Outbound> outbounds;
    private Routing routing;
    private Policy policy;

    @Data
    public static class LogConfig {
        private String loglevel;
        private String access;
        private String error;
    }


    @Data
    public static class Inbound {
        private Object listen;
        private Integer port;
        private String protocol;
        private InboundSettings settings;
        private StreamSettings streamSettings;
        private Sniffing sniffing;
    }

    @Data
    public static class InboundSettings {
        private List<Client> clients;
        private String decryption;
    }

    @Data
    public static class Client {
        private String id;
        private String flow;
    }

    @Data
    public static class StreamSettings {
        private String network;
        private String security;
        private RealitySettings realitySettings;
    }

    @Data
    public static class RealitySettings {
        private Boolean show;
        private String dest;
        private Integer xver;
        private List<String> serverNames;
        private String privateKey;
        private List<String> shortIds;
    }

    @Data
    public static class Sniffing {
        private Boolean enabled;
        private List<String> destOverride;
    }

    @Data
    public static class Outbound {
        private String tag;
        private String protocol;
        private Object settings;
    }

    @Data
    public static class Routing {
        private List<Rule> rules;
    }

    @Data
    public static class Rule {
        private String type;
        private String outboundTag;
        private String network;
        private List<String> ip;
    }

    @Data
    public static class Policy {
        private Boolean statsInboundUplink;
        private Boolean statsInboundDownlink;
        private Boolean statsOutboundUplink;
        private Boolean statsOutboundDownlink;
    }
}