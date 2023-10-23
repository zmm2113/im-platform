package com.platform.common.upload.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.platform.common.upload.config.UploadConfig;
import com.platform.common.upload.enums.UploadTypeEnum;
import com.platform.common.upload.service.UploadService;
import com.platform.common.upload.vo.UploadFileVo;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;

/**
 * 七牛云上传
 */
@Slf4j
@Service("uploadKodoService")
@Configuration
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "kodo")
public class UploadKodoServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    /**
     * 获取Auth
     */
    private Auth getAuth() {
        return Auth.create(uploadConfig.getAccessKey(), uploadConfig.getSecretKey());
    }

    /**
     * 获取Token
     */
    private String getUploadToken(String fileType) {
        return getAuth().uploadToken(uploadConfig.getBucket(), fileType);
    }

    @Override
    public String getServerUrl() {
        return uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        String serverUrl = uploadConfig.getServerUrl();
        String region = uploadConfig.getRegion();
        String post = uploadConfig.getPost();
        String fileName = IdUtil.objectId();
        String token;
        if (StringUtils.isEmpty(fileType)) {
            token = getUploadToken(fileType);
        } else {
            fileName += "." + fileType;
            token = getUploadToken(fileName);
        }
        return Dict.create()
                .set("uploadType", UploadTypeEnum.KODO)
                .set("serverUrl", serverUrl)
                .set("fileName", fileName)
                .set("region", region)
                .set("uploadToken", token)
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
        String token = getUploadToken(fileKey);
        Response response = null;
        try {
            UploadManager uploadManager = new UploadManager(new com.qiniu.storage.Configuration());
            response = uploadManager.put(file.getInputStream(), fileKey, token, null, fileType);
            return format(fileName, uploadConfig.getServerUrl(), fileKey, fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            if (response != null) {
                response.close();
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
        String token = getUploadToken(fileKey);
        InputStream inputStream = FileUtil.getInputStream(file);
        Response response = null;
        try {
            UploadManager uploadManager = new UploadManager(new com.qiniu.storage.Configuration());
            response = uploadManager.put(inputStream, fileKey, token, null, fileType);
            return format(fileName, uploadConfig.getServerUrl(), fileKey, fileType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
