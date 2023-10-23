package com.platform.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别类型枚举
 */
@Getter
public enum GenderEnum {

    /**
     * 未知
     */
    UNKNOWN("0", "未知"),
    /**
     * 男
     */
    MALE("1", "男"),
    /**
     * 女
     */
    FEMALE("2", "女");

    @EnumValue
    @JsonValue
    private final String code;
    private final String info;

    GenderEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
