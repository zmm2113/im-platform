package com.platform.common.version;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 版本枚举值
 */
@Getter
public enum VersionEnum {

    V1_0_0("1.0.0", "初始化版本"),
    V1_0_1("1.0.1", "下一个版本"),
    ;

    @EnumValue
    @JsonValue
    private final String code;
    private final String info;

    VersionEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
