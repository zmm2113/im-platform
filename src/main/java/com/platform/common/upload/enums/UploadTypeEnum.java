package com.platform.common.upload.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 上传类型枚举
 */
@Getter
public enum UploadTypeEnum {

    /**
     * 阿里oss
     */
    OSS("oss", "阿里oss"),

    /**
     * 腾讯cos
     */
    COS("cos", "腾讯cos"),

    /**
     * 七牛kodo
     */
    KODO("kodo", "七牛kodo"),

    /**
     * FAST
     */
    FAST("fast", "FAST_DFS"),

    /**
     * MINIO
     */
    MINIO("minio", "MINIO"),

    /**
     * 本地local
     */
    LOCAL("local", "本地上传"),

    ;

    @JsonValue
    private final String code;
    private final String info;

    UploadTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
