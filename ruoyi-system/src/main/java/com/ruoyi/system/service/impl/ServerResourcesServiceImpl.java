package com.ruoyi.system.service.impl;


import cn.hutool.core.lang.Dict;
import cn.hutool.core.net.url.UrlPath;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.AvailableStatus;
import com.ruoyi.system.constant.ResourcesStatus;
import com.ruoyi.system.constant.SalesStatus;
import com.ruoyi.system.domain.dto.ResourceProcessingDto;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.domain.entity.XrayOutbound.OutboundConfig;
import com.ruoyi.system.domain.vo.ResourcesImportVo;
import com.ruoyi.system.domain.vo.ServerResourcesPageVo;
import com.ruoyi.system.domain.vo.VendorAccountInformationVo;
import com.ruoyi.system.domain.vo.XrayRestartVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.mapstruct.ServerResourcesMapstruct;
import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.NetworkUtil;
import com.ruoyi.system.util.XrayManager;
import lombok.Cleanup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 服务器资源表(ServerResources)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Slf4j
@Service("serverResourcesService")
public class ServerResourcesServiceImpl  implements IServerResourcesService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private ServerResourcesMapstruct serverResourcesMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private VendorAccountInformationMapper vendorAccountInformationMapper;

    @Resource
    private ServerResourcesXrayValidMapper serverResourcesXrayValidMapper;

    @Resource
    private ResourceAllocationTemporaryStorageMapper resourceAllocationTemporaryStorageMapper;

    @Value("${resources.clashUrl}")
    private String clashDownloadUrl;




    /**
     * [新增服务器资源]
     *
     * @param serverResourcesDto 服务器资源
     * @return com.ruoyi.system.domain.entity.ServerResources
     * @author 陈湘岳 2025/7/23
     **/
    @Override
    public Result<ServerResources> insert(ServerResourcesDto serverResourcesDto) {
        ServerResources serverResources = serverResourcesMapstruct.changeDto2(serverResourcesDto);
        QueryWrapper<Commodity> commodityWrapper = new QueryWrapper<>();
        commodityWrapper.eq("id",serverResources.getCommodityId());
        commodityWrapper.eq("is_deleted",0);
        //校验所属商品是否存在
        Commodity commodity = commodityMapper.selectOne(commodityWrapper);
        if (ObjectUtils.isEmpty( commodity)){
            return Result.fail("所选商品不存在");
        }
        int insert = serverResourcesMapper.insert(serverResourcesBuild(serverResources));
        if (insert > 0){
            return Result.success(serverResources);
        } else {
            return Result.fail("新增失败");
        }
    }

    /**
     * [处理节点上报资源]
     *
     * @param resourceProcessingDto 上报参数
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.entity.ServerResources>
     * @author 陈湘岳 2025/9/16
     **/
    @Override
    @Transactional
    public Result<ServerResources> resourceProcessing(ResourceProcessingDto resourceProcessingDto) {
        ResourceAllocationTemporaryStorage resourceAllocationTemporaryStorage = resourceAllocationTemporaryStorageMapper.selectByIp(resourceProcessingDto.getResourcesIp());
        if (resourceAllocationTemporaryStorage == null){
            return Result.fail("该ip资源不存在");
        }

        ServerResources byIpServerResources = serverResourcesMapper.findByIpServerResources(resourceProcessingDto.getResourcesIp());
        if (byIpServerResources != null){
            int deleteByIp = resourceAllocationTemporaryStorageMapper.deleteByIp(resourceProcessingDto.getResourcesIp());
            return Result.fail("该ip已存在资源池");
        }
        Commodity commodity = commodityMapper.selectById(resourceProcessingDto.getCommodityId());
        if (commodity == null||commodity.getIsDeleted()==1){
            return Result.fail("所选商品不存在");
        }
        VendorAccountInformationVo vendorAccountInformationVo = vendorAccountInformationMapper.queryById(resourceProcessingDto.getVendorAccountId());
        if (vendorAccountInformationVo == null){
            return Result.fail("所选云服务商账号不存在");
        }

        String userId = UUID.randomUUID().toString();

        String post = XrayManager.restartXray(commodity.getDest(), commodity.getServerNames(),
                resourceAllocationTemporaryStorage.getNodePort(), userId, resourceProcessingDto.getResourcesIp());


        if ("JsonError".contains( post)|| "RequestFieldError".contains(post)){
            return Result.fail("参数错误，请联系管理员");
        }
        if ("XrayStartError".contains(post)){
            return Result.fail("节点启动失败");
        }
        XrayRestartVo xrayRestartVo =null;
        //反参
        try{
             xrayRestartVo = JSON.parseObject(post, XrayRestartVo.class);
        }catch (Exception e){
            return Result.fail("节点启动失败");
        }


        //新增资源数据
        ServerResources serverResources = buildServerResources(userId, commodity, xrayRestartVo, resourceAllocationTemporaryStorage, resourceProcessingDto);
        int insert = serverResourcesMapper.insert(serverResources);

        //删除暂存数据
        int deleteByIp = resourceAllocationTemporaryStorageMapper.deleteByIp(resourceProcessingDto.getResourcesIp());

        if (insert > 0){
            //新增资源校验数据
            String strUserId = SecurityUtils.getStrUserId();
            CompletableFuture.runAsync(() -> {
                newResourcesValid(userId, serverResources,strUserId);
            });
            return Result.success(serverResources);
        }else{

            return Result.fail("新增失败");
        }
    }



    private void newResourcesValid(String userId,ServerResources serverResources,String strUserId){

        String cacheObject = redisCache.getCacheObject("sys_config:sys:validServer:ip");
        if (StrUtil.isBlank(serverResources.getId())||StrUtil.isBlank(cacheObject)){
            //如果id为空或者校验服务器ip为空则不新增资源校验
            return;
        }
        List<String> ipAndPortList = StrUtil.split(cacheObject, ",");
        Map<String, IpUseNum> useLeastIp = serverResourcesXrayValidMapper.findUseLeastIp(ipAndPortList);
        String minUseIp=ipAndPortList.get(0);
        for (Map.Entry<String, IpUseNum> entry : useLeastIp.entrySet()) {
            if (minUseIp.isEmpty()||entry.getValue().getUsageCount()<useLeastIp.get(minUseIp).getUsageCount()){
                minUseIp = entry.getKey();
            }
        }
        log.info("[新增资源校验]开始");
        log.info("[新增资源校验]使用最少的ip为："+minUseIp);
        //新增资源校验
        OutboundConfig outboundConfig = OutboundConfig.buildOutboundConfig(userId, serverResources.getResourcesIp(),
                Integer.parseInt(serverResources.getNodePort()), serverResources.getId(), serverResources.getSni(),
                serverResources.getPublicBrokerKey(), serverResources.getShortId());
        String newValidXrayResult = XrayManager.newValidXray(outboundConfig, minUseIp);
        if (StrUtil.isBlank(newValidXrayResult)){
            log.error("调用go[newValidXray]接口失败");
            return;
        }
        Result result = JSON.parseObject(newValidXrayResult, Result.class);
        if (result.getCode()!=200){
            log.error("调用go[newValidXray]接口失败，错误原因为"+result.getMessage());
            return;
        }
        ServerResourcesXrayValid serverResourcesXrayValid = new ServerResourcesXrayValid();
        serverResourcesXrayValid.setWebIpPort(minUseIp);
        serverResourcesXrayValid.setXrayPort(result.getData().toString());
        serverResourcesXrayValid.setResourcesId(serverResources.getId());
        serverResourcesXrayValid.setCreateUser(strUserId);
        serverResourcesXrayValid.setUpdateUser(strUserId);
        serverResourcesXrayValidMapper.deleteByResourcesIdInt(serverResources.getId());
        int insert = serverResourcesXrayValidMapper.insert(serverResourcesXrayValid);
    }



    /**
     * [资源分页查询]
     *
     * @param serverResourcesPageDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/17
     **/
    @Override
    public Result getResourcesPage(ServerResourcesPageDto serverResourcesPageDto) {
        PageHelper.startPage(serverResourcesPageDto);
        List<ServerResourcesPageVo> serverResourcesVos = serverResourcesMapper.findPage(serverResourcesPageDto);
        return Result.success(new PageInfo<>(serverResourcesVos));
    }

    private ServerResources buildServerResources(String userId, Commodity commodity, XrayRestartVo xrayRestartVo, ResourceAllocationTemporaryStorage byIp, ResourceProcessingDto resourceProcessingDto){
        ServerResources serverResources = new ServerResources();
        serverResources.setResourcesIp(resourceProcessingDto.getResourcesIp());
        serverResources.setResourcesPort(resourceProcessingDto.getResourcesPort());
        serverResources.setResourcesPassword(resourceProcessingDto.getResourcesPassword());
        serverResources.setResourcesUserName(resourceProcessingDto.getResourcesUserName());
        serverResources.setResourcesStatus(ResourcesStatus.WAIT_CHECK);
        serverResources.setSalesStatus(SalesStatus.NOT_SALE);
        serverResources.setAvailableStatus(AvailableStatus.AVAILABLE_STATUS_DOWN);
        serverResources.setVendorAccountId(resourceProcessingDto.getVendorAccountId());
        serverResources.setNodePort(byIp.getNodePort().toString());
        serverResources.setPublicBrokerKey(xrayRestartVo.getPublicKey());
        serverResources.setSni(commodity.getServerNames());
        serverResources.setShortId(xrayRestartVo.getShortId());
        serverResources.setPassword(generateSecureRandomString(20));
        serverResources.setUserId(userId);
        serverResources.setCommodityId(commodity.getId());
        return serverResources;
    }


    private ServerResources serverResourcesBuild(ServerResources serverResources){
        serverResources.setResourcesStatus(ResourcesStatus.WAIT_NOTICE_DATA);
        return serverResources;
    }


    // 方法1: 使用SecureRandom生成安全随机字符串
    public static String generateSecureRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        // 使用Base64编码确保可打印字符
        String base64 = Base64.getEncoder().encodeToString(bytes);

        // 移除Base64的填充字符并截取所需长度
        return base64.replace("=", "").substring(0, length);
    }

    /**
     * [下载clash配置文件]
     *
     * @return org.springframework.http.ResponseEntity
     * @author 陈湘岳 2025/9/17
     **/
    @Override
    public ResponseEntity download(String password) {
        ServerResources serverResources = serverResourcesMapper.selectByPassword(password);
        if (ObjectUtils.isEmpty(serverResources)){
            throw new BaseException("资源不存在");
        }
        try {
            ClassPathResource resource = new ClassPathResource("templates/Config.yaml");
            if (resource.exists()) {
                @Cleanup
                InputStream inputStream = resource.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                // 通过循环从输入流中读取字节数据到字节数组输出流中
                while ((length = inputStream.read(buffer))!= -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                byte[] bytes = byteArrayOutputStream.toByteArray();
                String template = byteArrayOutputStream.toString();
                String replaceTemplate = replaceTemplate(template, serverResources);

                // 设置响应头，包括文件名等信息，让浏览器能正确识别并下载文件
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Config.yaml");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                // 返回响应实体，包含文件字节数据、响应头以及状态码
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(replaceTemplate);
            }
        } catch (IOException e) {
            throw new RuntimeException("下载导入模板失败");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * [获取导入链接]
     *
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/22
     **/
    @Override
    public Result getImportUrl(String resourcesId,Boolean hasAuth) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
        if (ObjectUtils.isEmpty(serverResources)){
            throw new BaseException("资源不存在");
        }
        if (!SecurityUtils.getStrUserId().equals(serverResources.getResourceTenant())){
            if (!hasAuth){
                throw new BaseException("没有权限查询该资源导入信息");
            }
        }
        ResourcesImportVo resourcesImportVo = new ResourcesImportVo();
        String clashDownloadUrl1 = getClashDownloadUrl(serverResources);
        resourcesImportVo.setClashDownloadUrl(clashDownloadUrl1);
        resourcesImportVo.setVlessUrl(getVlessUrl(serverResources));
        return Result.success(resourcesImportVo);
    }


    private String getClashDownloadUrl(ServerResources serverResources) {
        return clashDownloadUrl+"?password="+serverResources.getPassword();
    }


    /**
     * [重置资源]
     *
     * @param id 资源id
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.entity.ServerResources>
     * @author 陈湘岳 2025/9/24
     **/
    @Override
    public Result<ServerResources> resourceReset(String id) {
        ServerResources serverResources = serverResourcesMapper.selectById(id);
        if (ObjectUtils.isEmpty(serverResources)){
            throw new BaseException("资源不存在");
        }

        Commodity commodity = commodityMapper.selectById(serverResources.getCommodityId());
        if (commodity == null||commodity.getIsDeleted()==1){
            return Result.fail("所选商品不存在");
        }

        String userId = UUID.randomUUID().toString();

        String post = XrayManager.restartXray(commodity.getDest(), commodity.getServerNames(),
                Integer.parseInt(serverResources.getNodePort()), userId, serverResources.getResourcesIp());


        if ("JsonError".contains( post)|| "RequestFieldError".contains(post)){
            return Result.fail("参数错误，请联系管理员");
        }
        if ("XrayStartError".contains(post)){
            return Result.fail("节点启动失败");
        }
        XrayRestartVo xrayRestartVo =null;
        //反参
        try{
            xrayRestartVo = JSON.parseObject(post, XrayRestartVo.class);
        }catch (Exception e){
            return Result.fail("节点启动失败");
        }

        serverResources.setPublicBrokerKey(xrayRestartVo.getPublicKey());
        serverResources.setShortId(xrayRestartVo.getShortId());
        serverResources.setUserId(userId);
        serverResources.setResourcesStatus(ResourcesStatus.WAIT_CHECK);
        //新增资源数据
        int update = serverResourcesMapper.updateById(serverResources);

        if (update > 0){
            //新增资源校验数据
            String strUserId = SecurityUtils.getStrUserId();
            CompletableFuture.runAsync(() -> {
                newResourcesValid(userId, serverResources, strUserId);
            });
            return Result.success(serverResources);
        }else{

            return Result.fail("新增失败");
        }

    }


    /**
     * [网络连通性检测，检测与资源节点之间的网络联通性]
     *
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/26
     **/
    @Override
    public Result networkConnectivityTesting(String resourcesId) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
        if (ObjectUtils.isEmpty(serverResources)){
            return Result.fail("资源不存在");
        }
        //检测网络是否联通
        boolean checkConnectivity = NetworkUtil.checkConnectivity(serverResources.getResourcesIp());
        return checkConnectivity?Result.success(true):Result.fail(false);
    }


    /**
     * [获取检测服务状态]
     *
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/26
     **/
    @Override
    public Result getXrayPing(String resourcesId) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
        if (ObjectUtils.isEmpty(serverResources)){
            return Result.fail("资源不存在");
        }
        String xrayPing = XrayManager.getXrayPing(serverResources.getResourcesIp());
        if (StrUtil.isBlank(xrayPing)){
            return  Result.success(false);
        }
        Result result = JSON.parseObject(xrayPing, Result.class);
        if (!ObjectUtils.isEmpty( result)&&"success".equals(result.getData())){
            return Result.success(true);
        }
        return Result.success(false);
    }

    /**
     * [检测资源节点防火墙是否开放xray端口]
     *
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/26
     **/
    @Override
    public Result getXrayFirewalld(String resourcesId) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
        if (ObjectUtils.isEmpty(serverResources)){
            return Result.fail("资源不存在");
        }
        String xrayFirewalldStatus = XrayManager.getXrayFirewalldStatus(serverResources.getResourcesIp(),serverResources.getNodePort());
        if (StrUtil.isBlank(xrayFirewalldStatus)){
            return  Result.success(false);
        }
        Result result = JSON.parseObject(xrayFirewalldStatus, Result.class);
        if (!ObjectUtils.isEmpty( result)&&Boolean.TRUE==result.getData()){
            return Result.success(true);
        }
        return Result.success(false);
    }


    /**
     * [获取资源节点xray进程状态]
     *
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/26
     **/
    @Override
    public Result getXrayProcess(String resourcesId) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
        if (ObjectUtils.isEmpty(serverResources)){
            return Result.fail("资源不存在");
        }
        String xrayFirewalldStatus = XrayManager.getXrayProcessStatus(serverResources.getResourcesIp(),serverResources.getNodePort());
        if (StrUtil.isBlank(xrayFirewalldStatus)){
            return  Result.success(false);
        }
        Result result = JSON.parseObject(xrayFirewalldStatus, Result.class);
        if (!ObjectUtils.isEmpty( result)&&"success".equals(result.getData())){
            return Result.success(true);
        }
        return Result.success(false);
    }

    /**
     * [通过出站节点查看是否可以通过目标代理节点访问谷歌]
     *
     * @param resourcesId
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/25
     **/
    @Override
    @Transactional
    public Result getResourcesStatus(String resourcesId) {
        ServerResourcesXrayValid byResourcesId = serverResourcesXrayValidMapper.findByResourcesId(resourcesId);
        if (byResourcesId == null){
            // 资源不存在  新增资源
            ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
            if (ObjectUtils.isEmpty(serverResources)){
                return Result.fail("资源不存在");
            }
            newResourcesValid(serverResources.getUserId(), serverResources, SecurityUtils.getStrUserId());
            byResourcesId = serverResourcesXrayValidMapper.findByResourcesId(resourcesId);
        }

        String xrayStatus = XrayManager.checkXrayStatus(byResourcesId.getWebIpPort(), byResourcesId.getXrayPort());
        Result result = JSON.parseObject(xrayStatus, Result.class);
        if (result.getCode()!=200){
            log.error("调用go[newValidXray]接口失败，错误原因为"+result.getMessage());
            return Result.fail("查询节点状态失败");
        }

        log.info("调用go[newValidXray]接口成功，返回结果为"+result.getData());
        String str= result.getData().toString();
        boolean equals = "200".equals(str.trim());
        if (equals){
            serverResourcesMapper.updateResourcesStatus(resourcesId,ResourcesStatus.NORMAL);
            return Result.success("节点状态正常");
        }else {
            serverResourcesMapper.updateResourcesStatus(resourcesId,ResourcesStatus.ERROR);
            return Result.success("节点状态异常");
        }
    }

    private String getVlessUrl(ServerResources serverResources){
// 构建基础 URL
        StringBuilder url = new StringBuilder("vless://");

        // 添加用户ID和服务器地址
        url.append(serverResources.getUserId())
                .append("@")
                .append(serverResources.getResourcesIp())
                .append(":")
                .append(serverResources.getNodePort())
                .append("?");

        List<String> split = StrUtil.split(serverResources.getSni(), ",");
        // 添加固定参数
        url.append("encryption=none")
                .append("&flow=xtls-rprx-vision")
                .append("&security=reality")
                .append("&sni=").append(encode(split.get(0)))
                .append("&fp=chrome")
                .append("&pbk=").append(encode(serverResources.getPublicBrokerKey()))
                .append("&sid=").append(encode(serverResources.getShortId()))
                // URL 编码后的 "/"
                .append("&spx=%2F")
                .append("&type=tcp")
                .append("&headerType=none");
        String commodityNameByResourcesId = commodityMapper.findCommodityNameByResourcesId(serverResources.getId());
        // 添加备注（URL 编码）
        if (StrUtil.isNotEmpty(commodityNameByResourcesId)) {
            url.append("#").append(encode(commodityNameByResourcesId));
        }

        return url.toString();
    }


    // URL 编码辅助方法
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }




    //替换模板
    private String replaceTemplate(String template, ServerResources serverResources) {
        String commodityNameByResourcesId = commodityMapper.findCommodityNameByResourcesId(serverResources.getId());
        List<String> split = StrUtil.split(serverResources.getSni(), ",");
        // 创建参数字典
        Dict params = Dict.create()
                .set("commodityName", commodityNameByResourcesId)
                .set("ip", serverResources.getResourcesIp())
                .set("port", serverResources.getNodePort())
                .set("uuid", serverResources.getUserId())
                .set("publicKey", serverResources.getPublicBrokerKey())
                .set("shortId", serverResources.getShortId())
                .set("serverName", split.get(0));
        return replaceTemplate(template, params);
    }

    public static String replaceTemplate(String template, Dict params) {
        // 创建模板引擎
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());

        // 使用模板引擎处理
        Template tpl = engine.getTemplate(template);
        return tpl.render(params);
    }
}
