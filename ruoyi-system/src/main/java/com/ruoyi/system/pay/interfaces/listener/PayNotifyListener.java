package com.ruoyi.system.pay.interfaces.listener;

import com.ruoyi.system.pay.application.vo.PayNotifyVo;
import com.ruoyi.system.pay.infrastructure.client.PayPlatformClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * [支付回调监听器]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Slf4j
@RestController
@RequestMapping("/pay")
public class PayNotifyListener {

    @Resource
    private PayPlatformClient payPlatformClient;

    /**
     * 异步通知处理
     */
    @PostMapping("/notify")
    public void handleAsyncNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到支付异步通知");

        // 获取所有参数
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) {
                params.put(k, v[0]);
            }
        });

        log.debug("Notify params: {}", params);

        // 验签
        Map<String, Object> responseMap = new HashMap<>(params);
        boolean verified = payPlatformClient.verifyResponse(responseMap);

        if (!verified) {
            log.error("异步通知验签失败");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 构建通知VO
        PayNotifyVo notifyVo = new PayNotifyVo();
        notifyVo.setPid(Integer.valueOf(params.get("pid")));
        notifyVo.setTradeNo(params.get("trade_no"));
        notifyVo.setOutTradeNo(params.get("out_trade_no"));
        notifyVo.setType(params.get("type"));
        notifyVo.setName(params.get("name"));
        notifyVo.setMoney(params.get("money"));
        notifyVo.setTradeStatus(params.get("trade_status"));
        notifyVo.setParam(params.get("param"));
        notifyVo.setTimestamp(params.get("timestamp"));
        notifyVo.setSign(params.get("sign"));
        notifyVo.setSignType(params.get("sign_type"));

        // 处理支付成功
        if (notifyVo.isTradeSuccess()) {
            log.info("支付成功，商户订单号：{}，平台订单号：{}",
                    notifyVo.getOutTradeNo(), notifyVo.getTradeNo());
            // TODO: 调用业务系统处理支付成功逻辑
        }

        // 返回success表示接收成功
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("success");
        writer.flush();
    }


    @GetMapping("/test")
    public void test(){
        Map<String, String> params = new HashMap<>();
        params.put("type","alipay");
        params.put("account","a1152581244@163.com");
        params.put("money","11");

        payPlatformClient.transfer(params);
    }


    /**
     * 同步跳转处理
     */
    @GetMapping("/return")
    public void handleSyncReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到支付同步跳转");

        // 获取所有参数
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) {
                params.put(k, v[0]);
            }
        });

        log.debug("Return params: {}", params);

        // 验签
        Map<String, Object> responseMap = new HashMap<>(params);
        boolean verified = payPlatformClient.verifyResponse(responseMap);

        if (!verified) {
            log.error("同步跳转验签失败");
            response.sendRedirect("/pay/error?msg=验签失败");
            return;
        }

        String tradeStatus = params.get("trade_status");
        String outTradeNo = params.get("out_trade_no");

        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            log.info("支付成功，商户订单号：{}", outTradeNo);
            response.sendRedirect("/pay/success?outTradeNo=" + outTradeNo);
        } else {
            log.info("支付未完成，商户订单号：{}，状态：{}", outTradeNo, tradeStatus);
            response.sendRedirect("/pay/fail?outTradeNo=" + outTradeNo);
        }
    }
}
