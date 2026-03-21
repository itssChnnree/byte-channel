package com.ruoyi.system.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.system.domain.dto.RestartXrayDto;
import com.ruoyi.system.domain.entity.ServerResourcesXrayValid;
import com.ruoyi.system.domain.entity.XrayOutbound.OutboundConfig;
import com.ruoyi.system.mapper.ServerResourcesXrayValidMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

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
@Slf4j
public class XrayManager {


    //重启xray
    //在节点服务启动xray
    public static String restartXray(String dest, String serverNames, Integer port,
                                     String userId,String resourcesIp,List<String> blockDomains) {
        RestartXrayDto restartXrayDto = new RestartXrayDto();
        restartXrayDto.setDest(dest);
        List<String> split = StrUtil.split(serverNames, ",");
        restartXrayDto.setServerNames(split);
        restartXrayDto.setPort(port);
        restartXrayDto.setUserId(userId);
        restartXrayDto.setDomains(blockDomains);
        //调用节点方法启动节点
        String post;
        try {
            String requestBody = JSON.toJSONString(restartXrayDto);
            post = HttpUtil.post("http://" + resourcesIp + ":9080/xrayRestart", requestBody);
        } catch (Exception e) {
            LogEsUtil.error("重置失败，节点服务异常"+e.getMessage(),e);
            throw new BaseException("重置失败，节点服务异常");
        }

        return post;
    }


    //新增xray校验
    //在出站节点服务新增出站规则，入站规则，路由规则
    public static String newValidXray(OutboundConfig outboundConfig,String ipAndPort){
        String api = "http://" + ipAndPort + "/newValidXray";
        LogEsUtil.info("新增xray校验调用url["+api+"]");
        String post;
        try {
            post = HttpUtil.post(api, JSON.toJSONString(outboundConfig),5000);
        } catch (Exception e) {
            LogEsUtil.error("新增校验节点资源校验数据异常"+e.getMessage(),e);
            throw new BaseException("新增校验节点资源校验数据异常");
        }
        return post;
    }

    //删除出站校验
    public static String deleteValidXray(ServerResourcesXrayValid serverResourcesXrayValid) {
        // 构建 URL：http://ip:9080/delete?tag=xxx
        String api = "http://" + serverResourcesXrayValid.getWebIpPort() + ":9080/delete?tag=" + serverResourcesXrayValid.getResourcesId();

        LogEsUtil.info("删除xray资源校验[" + api + "]");

        try {
            // 执行 DELETE 请求并获取响应体
            String result = HttpRequest.delete(api)
                    // 5秒超时
                    .timeout(5000)
                    .execute()
                    .body();

            LogEsUtil.info("删除xray资源校验成功，响应：" + result);
            return result;

        } catch (Exception e) {
            LogEsUtil.error("删除xray资源校验异常：" + e.getMessage(), e);
            return null;
        }
    }


    //通过出站节点服务查看通过代理节点是否可ping通谷歌
    public static String checkXrayStatus(String ipAndPort,String inboundPort){
        String api = "http://"+ipAndPort+"/CheckXrayStatus?port="+inboundPort;
        LogEsUtil.info("xray校验调用url["+api+"]");
        String post;
        try {
            post = HttpUtil.get(api,5000);
        } catch (Exception e) {
            LogEsUtil.error("检测资源节点网络连通性异常"+e.getMessage(),e);
            throw new BaseException("检测资源节点网络连通性异常");
        }
        return post;
    }


    //向资源节点发送心跳，确认go服务状态
    public static String getXrayPing(String ip){
        String api = "http://"+ip+":9080/ping";
        LogEsUtil.info("xray-资源节点go检测调用url["+api+"]");
        String post;
        try {
            post = HttpUtil.get(api,5000);
        } catch (Exception e) {
            LogEsUtil.error("检测资源节点检测服务状态异常"+e.getMessage(),e);
            throw new BaseException("检测资源节点检测服务状态异常");
        }
        return post;
    }


    //查询资源节点对xray节点防火墙是否开放
    public static String getXrayFirewalldStatus(String ip,String port){
        String api = "http://"+ip+":9080/checkFirewalld?port="+ port;
        LogEsUtil.info("xray-资源节点防火墙调用url["+api+"]");
        String post;
        try {
            post = HttpUtil.get(api,5000);
        } catch (Exception e) {
            LogEsUtil.error("检测资源节点防火墙状态异常"+e.getMessage(),e);
            throw new BaseException("检测资源节点防火墙状态异常");
        }
        return post;
    }

    //修改节点屏蔽域名
    public static String updateBlockDomains(String ip,String domainList){
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        String url = "http://" + ip + ":9080/updateBlockDomains";
        String post;
        LogEsUtil.info("xray-资源节点修改屏蔽域名调用url["+url+"]");
        try {
            post = restTemplate.postForObject(url, domainList, String.class);
//            post = HttpUtil.post(url, domainList);
        } catch (Exception e) {
            LogEsUtil.error("修改["+ip+"]节点屏蔽域名异常"+e.getMessage(),e);
            return null;
        }
        return post;
    }


    //查询资源节点xray进程状态
    public static String getXrayProcessStatus(String ip,String port){
        String api = "http://"+ip+":9080/checkXrayStatus";
        LogEsUtil.info("xray-资源节点xray进程状态调用url["+api+"]");
        String post;
        try {
            post = HttpUtil.get(api,5000);
        } catch (Exception e) {
            LogEsUtil.error("检测资源节点xray状态异常"+e.getMessage(),e);
            throw new BaseException("检测资源节点xray状态异常");
        }
        return post;
    }

}
