package com.platform.modules.push.enums;

import lombok.Getter;

/**
 * 推送消息类型
 */
@Getter
public enum PushBodyEnum {

    /**
     * 普通消息
     */
    MSG("MSG", "普通消息"),
    /**
     * 通知消息
     */
    NOTICE("NOTICE", "通知消息"),
    ;

    private String code;
    private String info;

    PushBodyEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
