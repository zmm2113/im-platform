package com.platform.common.version;

import com.platform.common.core.EnumUtils;
import com.platform.common.enums.YesOrNoEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取项目相关配置
 */
@Component
@ConfigurationProperties(prefix = "version")
public class VersionConfig {

    /**
     * 版本开关
     */
    public static YesOrNoEnum ENABLED = YesOrNoEnum.NO;

    /**
     * 最低版本
     */
    public static String VERSION = "1.0.0";

    /**
     * 过滤请求
     */
    public static List<String> EXCLUDES = new ArrayList<>();

    /**
     * 过滤请求
     */
    public static List<String> BANS = new ArrayList<>();

    /**
     * 签名
     */
    public static Map<String, String> SIGN = new HashMap<>();

    public void setEnabled(String enabled) {
        if (!StringUtils.isEmpty(enabled)) {
            VersionConfig.ENABLED = EnumUtils.toEnum(YesOrNoEnum.class, enabled, YesOrNoEnum.NO);
        }
    }

    public void setVersion(String version) {
        if (!StringUtils.isEmpty(version)) {
            VersionConfig.VERSION = version;
        }
    }

    public void setExcludes(List<String> excludes) {
        if (!CollectionUtils.isEmpty(excludes)) {
            VersionConfig.EXCLUDES = excludes;
        }
    }

    public void setBans(List<String> bans) {
        if (!CollectionUtils.isEmpty(bans)) {
            VersionConfig.BANS = bans;
        }
    }

    public void setSign(Map<String, String> sign) {
        if (!CollectionUtils.isEmpty(sign)) {
            VersionConfig.SIGN = sign;
        }
    }
}