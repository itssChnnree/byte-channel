package com.ruoyi.system.util;


import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/3/11
 */
@Slf4j
public class EsUtil {

    public static String addTimeAndVersion(String json){
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(json);
        }catch (RuntimeException e){
            log.error("json转化失败");
            throw new RuntimeException("json转化失败");
        }
        //将当前时间转化为字符串

        jsonObject.put("timestamp", getCurrentTimeInBeijing());
        jsonObject.put("@version","1");
        return jsonObject.toJSONString();
    }

    public static String getCurrentTimeInBeijing() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为北京时间
        return sdf.format(new Date());
    }

    public static String addTimeAndVersion(JSONObject jsonObject){
        /*JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(json);
        }catch (RuntimeException e){
            log.error("json转化失败");
            throw new RuntimeException("json转化失败");
        }*/
        jsonObject.put("@timestamp",getCurrentTimeInBeijing());
        jsonObject.put("@version","1");
        return jsonObject.toJSONString();
    }


}
