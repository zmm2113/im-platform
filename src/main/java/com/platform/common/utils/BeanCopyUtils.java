package com.platform.common.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * java bean 复制操作的工具类
 */
public class BeanCopyUtils {

    /**
     * 包含方法
     */
    public static <T> T include(Object source, String... fields) {
        JSONObject jsonObject = JSONUtil.parseObj(source);
        JSONObject target = new JSONObject();
        for (String field : fields) {
            target.set(field.trim(), jsonObject.get(field.trim()));
        }
        return (T) target.toBean(source.getClass());
    }

    /**
     * 排除方法
     */
    public static <T> T exclude(Object source, String... fields) {
        JSONObject jsonObject = JSONUtil.parseObj(source);
        for (String field : fields) {
            jsonObject.remove(field.trim());
        }
        return (T) jsonObject.toBean(source.getClass());
    }

}
