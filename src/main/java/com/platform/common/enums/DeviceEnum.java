package com.platform.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 设备类型枚举
 */
@Getter
public enum DeviceEnum {

    /**
     * 安卓
     */
    ANDROID("android", "安卓"),
    /**
     * 苹果
     */
    IOS("ios", "苹果"),
    /**
     * 微软
     */
    WINDOWS("windows", "微软"),
    /**
     * MAC
     */
    MAC("mac", "MAC"),
    /**
     * H5
     */
    H5("h5", "h5"),
    /**
     * PC
     */
    PC("pc", "PC"),
    /**
     * 小程序
     */
    MINI("mini", "小程序"),
    ;

    @EnumValue
    @JsonValue
    private final String code;
    private final String info;

    DeviceEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
