package com.platform.common.upload.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.platform.common.upload.config.UploadConfig;
import com.platform.common.upload.enums.UploadTypeEnum;
import com.platform.common.upload.service.UploadService;
import com.platform.common.upload.utils.FastUtils;
import com.platform.common.upload.vo.UploadFileVo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * fast上传
 */
@Slf4j
@Service("uploadFastService")
@Configuration
@NoArgsConstructor
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "fast")
public class UploadFastServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    @Override
    public String getServerUrl() {
        return uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        String serverUrl = uploadConfig.getServerUrl();
        String post = uploadConfig.getPost();
        return Dict.create()
                .set("uploadType", UploadTypeEnum.LOCAL)
                .set("serverUrl", serverUrl)
                .set("fileName", IdUtil.objectId() + "." + fileType)
                .set("post", post);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        StorePath storePath;
        try {
            storePath = FastUtils.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        String fileKey = storePath.getFullPath();
        String fileName = getFileName(file);
        String fileType = getFileType(file);
        String serverUrl = uploadConfig.getServerUrl();
        return format(fileName, serverUrl, fileKey, fileType);
    }

    @Override
    public UploadFileVo uploadFile(File file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        StorePath storePath;
        try {
            storePath = FastUtils.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        String fileKey = storePath.getFullPath();
        String fileType = getFileType(file);
        String fileName = getFileName(file);
        String serverUrl = uploadConfig.getServerUrl();
        return format(fileName, serverUrl, fileKey, fileType);
    }

}
