package com.platform.common.version;

import lombok.Getter;

/**
 * 版本枚举值
 */
@Getter
public enum VersionEnum {

    V1_0_0("1.0.0", "初始化版本", "第一个版本"),
    V1_1_0("1.1.0", "初始化版本", "增加websocket"),
    ;

    private final String code;
    private final String info;
    private final String remark;

    VersionEnum(String code, String info, String remark) {
        this.code = code;
        this.info = info;
        this.remark = remark;
    }

}
