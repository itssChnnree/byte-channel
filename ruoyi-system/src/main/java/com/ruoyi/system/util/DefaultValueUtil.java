package com.ruoyi.system.util;

import com.ruoyi.system.anno.InitializeWith;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * [默认赋值]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/29
 */
public class DefaultValueUtil {


    public static void setDefaultData(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (CollectionUtils.isEmpty(Arrays.asList(fields))){
            return;
        }
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(InitializeWith.class)) {
                InitializeWith annotation = field.getAnnotation(InitializeWith.class);
                if (field.get(obj) == null) {
                    Class<?> type = field.getType();
                    if (type == String.class) {
                        field.set(obj, annotation.stringValue());
                    } else if (type == int.class || type == Integer.class) {
                        field.set(obj, annotation.intValue());
                    } else if (type == double.class || type == Double.class) {
                        field.set(obj, annotation.doubleValue());
                    }else if (type == boolean.class || type == Boolean.class) {
                        field.set(obj, annotation.booleanValue());
                    }
                    else if (type == long.class || type == Long.class) {
                        field.set(obj, annotation.longValue());
                    }
                    else if (type == float.class || type == Float.class) {
                        field.set(obj, annotation.floatValue());
                    }
                    else if (type == char.class || type == Character.class) {
                        field.set(obj, annotation.charValue());
                    }
                    else if (type == short.class || type == Short.class) {
                        field.set(obj, annotation.shortValue());
                    }
                    else if (type == byte.class || type == Byte.class) {
                        field.set(obj, annotation.byteValue());
                    }
                }
            }
        }
    }
}
