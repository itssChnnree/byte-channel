package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.Util.DefaultValueUtil;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderByShoppingCartDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 **/
@Api(tags = "订单表")
@RestController
@RequestMapping("order")
public class OrderController {

    @Resource
    IOrderService orderService;



    @PostMapping("/createOrderByCommodity")
    @ApiOperation("从商品创建订单")
    public Result createOrderByCommodity(@RequestBody @Validated(InsertGroup.class) OrderByCommodityDto orderByCommodityDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.createOrderByCommodity(orderByCommodityDto);
    }


    @GetMapping("/cancelOrder")
    @ApiOperation("取消订单")
    public Result cancelOrder(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.cancelOrder(orderId);
    }


    @GetMapping("/pageQuery")
    @ApiOperation("分页查询订单")
    public Result pageQuery(@RequestBody OrderDto orderDto){
        return orderService.pageQuery(orderDto);
    }


    @GetMapping("/getOrderInfo")
    @ApiOperation("获取订单信息")
    public Result getOrderInfo(String orderId){
        if (StrUtil.isBlank(orderId)) {
            return Result.fail("请选择订单");
        }
        return orderService.getOrderInfo(orderId);
    }


    @PostMapping("/calculatePrice")
    @ApiOperation("计算价格")
    public Result calculatePrice(@RequestBody @Validated(InsertGroup.class) OrderByCommodityDto orderByCommodityDto
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return orderService.calculatePrice(orderByCommodityDto);
    }



    @GetMapping("/test")
    public String test(){
        return orderService.test();
    }



    //下载文件
    @GetMapping("/download")
    public ResponseEntity download( String path,HttpServletResponse response){
        try {
            ClassPathResource resource = new ClassPathResource("file/" + path);
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
                // 设置响应头，包括文件名等信息，让浏览器能正确识别并下载文件
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path);
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                // 返回响应实体，包含文件字节数据、响应头以及状态码
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(bytes);
            }
        } catch (IOException e) {
            throw new RuntimeException("下载导入模板失败");
        }
        return ResponseEntity.notFound().build();
    }
}
