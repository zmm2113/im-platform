package com.platform.common.upload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件上传
 */
@Component
@Data
public class UploadConfig {

    /**
     * 服务端域名
     */
    @Value("${upload.serverUrl}")
    private String serverUrl;
    /**
     * accessKey
     */
    @Value("${upload.accessKey}")
    private String accessKey;
    /**
     * secretKey
     */
    @Value("${upload.secretKey}")
    private String secretKey;
    /**
     * bucket
     */
    @Value("${upload.bucket}")
    private String bucket;
    /**
     * region
     */
    @Value("${upload.region}")
    private String region;
    /**
     * 封面
     */
    @Value("${upload.post}")
    private String post;

}
