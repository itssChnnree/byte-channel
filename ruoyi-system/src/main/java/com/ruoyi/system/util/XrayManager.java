package com.ruoyi.system.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.system.domain.dto.RestartXrayDto;
import com.ruoyi.system.domain.entity.XrayOutbound.OutboundConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/23
 */
public class XrayManager {


    //重启xray
    public static String restartXray(String dest, String serverNames, Integer port, String userId,String resourcesIp){
        RestartXrayDto restartXrayDto = new RestartXrayDto();
        restartXrayDto.setDest(dest);
        List<String> split = StrUtil.split(serverNames, ",");
        restartXrayDto.setServerNames(split);
        restartXrayDto.setPort(port);
        restartXrayDto.setUserId(userId);
        //调用节点方法启动节点
        return HttpUtil.post("http://" + resourcesIp + ":9080/xrayRestart", JSON.toJSONString(restartXrayDto));
    }


    //新增xray校验
    public static String newValidXray(OutboundConfig outboundConfig,String ipAndPort){
        return HttpUtil.post("http://" + ipAndPort + "/newValidXray", JSON.toJSONString(outboundConfig));
    }



}
