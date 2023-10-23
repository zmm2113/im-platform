package com.platform.common.upload.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Dict;
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

/**
 * 本地上传
 */
@Slf4j
@Service("uploadLocalService")
@Configuration
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "local")
public class UploadLocalServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    @Override
    public String getServerUrl() {
        return uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        return Dict.create()
                .set("uploadType", UploadTypeEnum.LOCAL);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        String serverUrl = uploadConfig.getServerUrl();
        String uploadPath = uploadConfig.getRegion();
        String fileName = getFileName(file);
        String fileType = getFileType(file);
        // 文件路径
        String fileKey = getFileKey(uploadPath, fileType);
        try {
            // 文件拷贝
            file.transferTo(new File(uploadPath + FileNameUtil.UNIX_SEPARATOR + fileKey));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        // 组装对象
        UploadFileVo fileVo = format(fileName, serverUrl, fileKey, fileType)
                .setFullPath(serverUrl + fileKey);
        return fileVo;
    }

    @Override
    public UploadFileVo uploadFile(File file) {
        return uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        String serverUrl = uploadConfig.getServerUrl();
        String uploadPath = uploadConfig.getRegion();
        String fileName = getFileName(file);
        String fileType = getFileType(file);
        // 文件路径
        String fileKey = getFileKey(uploadPath, fileType);
        // 文件拷贝
        FileUtil.copyFile(file, new File(uploadPath + FileNameUtil.UNIX_SEPARATOR + fileKey));
        // 组装对象
        UploadFileVo fileVo = format(fileName, serverUrl, fileKey, fileType)
                .setFullPath(serverUrl + fileKey);
        return fileVo;
    }

}
