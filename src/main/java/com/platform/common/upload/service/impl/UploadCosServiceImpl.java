package com.platform.common.upload.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.common.upload.config.UploadConfig;
import com.platform.common.upload.enums.UploadTypeEnum;
import com.platform.common.upload.service.UploadService;
import com.platform.common.upload.vo.UploadFileVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;

/**
 * 腾讯云上传
 */
@Slf4j
@Service("uploadCosService")
@Configuration
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "cos")
public class UploadCosServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    /**
     * 初始化cos
     */
    private COSClient initCOS() {
        return new COSClient(new BasicCOSCredentials(uploadConfig.getAccessKey(), uploadConfig.getSecretKey()),
                new com.qcloud.cos.ClientConfig(new Region(uploadConfig.getRegion())));
    }

    @Override
    public String getServerUrl() {
        return uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        // 1、默认固定值
        Long expire = 1800L;
        String algorithm = "sha1";
        String accessKey = uploadConfig.getAccessKey();
        String post = uploadConfig.getPost();
        String serverUrl = uploadConfig.getServerUrl();
        // 2、生成KeyTime
        Long startTime = DateUtil.currentSeconds();
        Long endTime = startTime + expire;
        String keyTime = StrUtil.format("{};{}", startTime, endTime);
        // 3、构造“策略”（Policy）
        JSONArray conditions = new JSONArray()
                .set(new JSONObject().set("q-sign-algorithm", algorithm))
                .set(new JSONObject().set("q-ak", accessKey))
                .set(new JSONObject().set("q-sign-time", keyTime));
        JSONObject policyObj = new JSONObject()
                .set("expiration", DateUtil.format(DateUtil.date(endTime * 1000), DatePattern.UTC_MS_PATTERN))
                .set("conditions", conditions);
        String policy = JSONUtil.toJsonStr(policyObj);
        // 4、生成 SignKey
        String signKey = SecureUtil.hmacSha1(uploadConfig.getSecretKey()).digestHex(keyTime);
        // 5、生成 Signature
        String signature = SecureUtil.hmacSha1(signKey).digestHex(SecureUtil.sha1(policy));
        return Dict.create()
                .set("uploadType", UploadTypeEnum.COS)
                .set("serverUrl", serverUrl)
                .set("fileName", IdUtil.objectId() + "." + fileType)
                .set("accessKey", accessKey)
                .set("policy", Base64.encode(policy))
                .set("signature", signature)
                .set("keyTime", keyTime)
                .set("algorithm", algorithm)
                .set("post", post);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        String fileName = getFileName(file);
        String fileKey = getFileKey(file, folder);
        String fileType = getFileType(file);
        // 3 生成 cos 客户端。
        COSClient client = null;
        try {
            client = initCOS();
            //上传到腾讯云
            PutObjectRequest putObjectRequest = new PutObjectRequest(uploadConfig.getBucket()
                    , fileKey, file.getInputStream(), new ObjectMetadata());
            client.putObject(putObjectRequest);
            return format(fileName, uploadConfig.getServerUrl(), fileKey, fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    @Override
    public UploadFileVo uploadFile(File file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        String fileName = getFileName(file);
        String fileKey = getFileKey(file, folder);
        String fileType = getFileType(file);
        InputStream inputStream = FileUtil.getInputStream(file);
        // 3 生成 cos 客户端。
        COSClient client = null;
        try {
            client = initCOS();
            //上传到腾讯云
            PutObjectRequest putObjectRequest = new PutObjectRequest(uploadConfig.getBucket()
                    , fileKey, inputStream, new ObjectMetadata());
            client.putObject(putObjectRequest);
            return format(fileName, uploadConfig.getServerUrl(), fileKey, fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

}
