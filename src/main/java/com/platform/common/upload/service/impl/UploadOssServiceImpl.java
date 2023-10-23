package com.platform.common.upload.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PolicyConditions;
import com.platform.common.upload.config.UploadConfig;
import com.platform.common.upload.enums.UploadTypeEnum;
import com.platform.common.upload.service.UploadService;
import com.platform.common.upload.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * 阿里云上传
 */
@Slf4j
@Service("uploadOssService")
@Configuration
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "oss")
public class UploadOssServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    /**
     * 初始化oss
     */
    private OSS initOSS() {
        return new OSSClientBuilder()
                .build(uploadConfig.getRegion(), uploadConfig.getAccessKey(), uploadConfig.getSecretKey());
    }

    @Override
    public String getServerUrl() {
        return uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        // 1、默认固定值，分钟
        Integer expire = 30;
        String accessKey = uploadConfig.getAccessKey();
        String serverUrl = uploadConfig.getServerUrl();
        String post = uploadConfig.getPost();
        // 2、过期时间
        Date expiration = DateUtil.offsetMinute(DateUtil.date(), expire);
        // 3、构造“策略”（Policy）
        OSS ossClient = initOSS();
        PolicyConditions policyConditions = new PolicyConditions();
        policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
        String postSignature = ossClient.calculatePostSignature(postPolicy);
        // 4、生成 Signature
        return Dict.create()
                .set("uploadType", UploadTypeEnum.OSS)
                .set("serverUrl", serverUrl)
                .set("fileName", IdUtil.objectId() + "." + fileType)
                .set("accessKey", accessKey)
                .set("policy", Base64.encode(postPolicy))
                .set("signature", postSignature)
                .set("post", post);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        OSS client = initOSS();
        try {
            String fileName = getFileName(file);
            String fileKey = getFileKey(file, folder);
            String fileType = getFileType(file);
            client.putObject(uploadConfig.getBucket(), fileKey, file.getInputStream());
            // 服务器地址
            String serverUrl = uploadConfig.getServerUrl();
            return format(fileName, serverUrl, fileKey, fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            client.shutdown();
        }
    }

    @Override
    public UploadFileVo uploadFile(File file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        OSS client = initOSS();
        try {
            String fileName = getFileName(file);
            String fileKey = getFileKey(file, folder);
            String fileType = getFileType(file);
            InputStream inputStream = FileUtil.getInputStream(file);
            client.putObject(uploadConfig.getBucket(), fileKey, inputStream);
            // 服务器地址
            String serverUrl = uploadConfig.getServerUrl();
            return format(fileName, serverUrl, fileKey, fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            client.shutdown();
        }
    }

}
