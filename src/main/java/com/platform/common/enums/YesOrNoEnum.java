package com.platform.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 是否类型枚举
 */
@Getter
public enum YesOrNoEnum {

    /**
     * 是
     */
    YES("Y", "是"),
    /**
     * 否
     */
    NO("N", "否"),
    /**
     * 退
     */
    REFUND("R", "退"),
    ;

    @EnumValue
    @JsonValue
    private final String code;
    private final String info;

    YesOrNoEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public static boolean transform(YesOrNoEnum result) {
        return YesOrNoEnum.YES.equals(result);
    }

    public static YesOrNoEnum transform(boolean result) {
        return result ? YesOrNoEnum.YES : YesOrNoEnum.NO;
    }

}
