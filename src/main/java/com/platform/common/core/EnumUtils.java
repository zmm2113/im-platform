package com.platform.common.core;

import cn.hutool.core.util.EnumUtil;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 类型转换器
 */
public class EnumUtils {

    public static <E extends Enum<E>> E toEnum(Class<E> clazz, String code) {
        return toEnum(clazz, code, null);
    }

    public static <E extends Enum<E>> E toEnum(Class<E> clazz, String code, E defaultValue) {
        if (StringUtils.isEmpty(code)) {
            return defaultValue;
        }
        Map<String, Object> enumMap = EnumUtil.getNameFieldMap(clazz, "code");
        for (String key : enumMap.keySet()) {
            if (code.equals(enumMap.get(key).toString())) {
                return EnumUtil.fromString(clazz, key);
            }
        }
        return defaultValue;
    }

}
