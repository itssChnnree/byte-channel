package com.ruoyi.system.service.impl;


import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.system.constant.AvailableStatus;
import com.ruoyi.system.constant.ResourcesStatus;
import com.ruoyi.system.constant.SalesStatus;
import com.ruoyi.system.domain.dto.ResourceProcessingDto;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.ResourceAllocationTemporaryStorage;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.domain.vo.ServerResourcesPageVo;
import com.ruoyi.system.domain.vo.VendorAccountInformationVo;
import com.ruoyi.system.domain.vo.XrayRestartVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.ResourceAllocationTemporaryStorageMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapper.VendorAccountInformationMapper;
import com.ruoyi.system.mapstruct.ServerResourcesMapstruct;
import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.XrayManager;
import lombok.Cleanup;
import org.springframework.core.io.ClassPathResource;
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
import java.util.List;
import java.util.UUID;

/**
 * 服务器资源表(ServerResources)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Service("serverResourcesService")
public class ServerResourcesServiceImpl  implements IServerResourcesService {


    @Resource
    private ServerResourcesMapstruct serverResourcesMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private VendorAccountInformationMapper vendorAccountInformationMapper;

    @Resource
    private ResourceAllocationTemporaryStorageMapper resourceAllocationTemporaryStorageMapper;




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

//        //重启xray请求体
//        RestartXrayDto restartXrayDto = new RestartXrayDto();
//        restartXrayDto.setDest(commodity.getDest());
//        List<String> split = StrUtil.split(commodity.getServerNames(), ",");
//        restartXrayDto.setServerNames(split);
//        restartXrayDto.setPort(resourceAllocationTemporaryStorage.getNodePort());
//        String userId = UUID.randomUUID().toString();
//        restartXrayDto.setUserId(userId);
//        //调用节点方法启动节点
//        String post = HttpUtil.post("http://" + resourceProcessingDto.getResourcesIp() + ":9080/xrayRestart", JSON.toJSONString(restartXrayDto));
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
        int deleteByIp = resourceAllocationTemporaryStorageMapper.deleteByIp(resourceProcessingDto.getResourcesIp());
        if (insert > 0){
            return Result.success(serverResources);
        }else{

            return Result.fail("新增失败");
        }
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
        serverResources.setUserId(userId);
        serverResources.setCommodityId(commodity.getId());
        return serverResources;
    }


    private ServerResources serverResourcesBuild(ServerResources serverResources){
        serverResources.setResourcesStatus(ResourcesStatus.WAIT_NOTICE_DATA);
        return serverResources;
    }


    /**
     * [下载clash配置文件]
     *
     * @param resourceId 资源id
     * @return org.springframework.http.ResponseEntity
     * @author 陈湘岳 2025/9/17
     **/
    @Override
    public ResponseEntity download(String resourceId) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourceId);
        if (ObjectUtils.isEmpty(serverResources)){
            throw new BaseException("资源不存在");
        }
        try {
            ClassPathResource resource = new ClassPathResource("templates/Config1.yaml");
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
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Config1.yaml");
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

    //替换模板
    private String replaceTemplate(String template, ServerResources serverResources) {
        List<String> split = StrUtil.split(serverResources.getSni(), ",");
        // 创建参数字典
        Dict params = Dict.create()
                .set("commodityName", "测试方法名")
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
