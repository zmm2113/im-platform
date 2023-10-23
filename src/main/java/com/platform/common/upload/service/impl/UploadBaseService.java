package com.platform.common.upload.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.platform.common.upload.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 基础上传
 */
@Slf4j
public class UploadBaseService {

    /**
     * 获取文件名称
     */
    protected static String getFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            // 随机名称
            fileName = IdUtil.objectId() + "." + getFileType(file);
        }
        return fileName;
    }

    /**
     * 获取文件名称
     */
    protected static String getFileName(File file) {
        String fileName = file.getName();
        if (StringUtils.isEmpty(fileName)) {
            // 随机名称
            fileName = IdUtil.objectId() + "." + getFileType(file);
        }
        return fileName;
    }

    /**
     * 获取文件全名
     */
    protected static String getFileKey(MultipartFile file) {
        return IdUtil.objectId() + "." + getFileType(file);
    }

    /**
     * 获取文件全名
     */
    protected static String getFileKey(MultipartFile file, String folder) {
        String fileKey = IdUtil.objectId() + "." + getFileType(file);
        if (StringUtils.isEmpty(folder)) {
            return fileKey;
        }
        return folder + "/" + fileKey;
    }

    /**
     * 获取文件全名
     */
    protected static String getFileKey(File file) {
        return IdUtil.objectId() + "." + getFileType(file);
    }

    /**
     * 获取文件全名
     */
    protected static String getFileKey(File file, String folder) {
        String fileKey = IdUtil.objectId() + "." + getFileType(file);
        if (StringUtils.isEmpty(folder)) {
            return fileKey;
        }
        return folder + "/" + fileKey;
    }

    /**
     * 获取文件全名
     */
    protected static String getFileKey(String uploadPath, String fileType) {
        // 文件路径
        String filePath = DateUtil.format(DateUtil.date(), "yyyy/MM/dd");
        // 生成文件夹
        FileUtil.mkdir(uploadPath + FileNameUtil.UNIX_SEPARATOR + filePath);
        return filePath + FileNameUtil.UNIX_SEPARATOR + IdUtil.objectId() + "." + fileType;
    }

    /**
     * 获取文件全名
     */
    protected static String getFileType(MultipartFile file) {
        // 文件扩展名
        String fileType = FileNameUtil.extName(file.getOriginalFilename());
        if (StringUtils.isEmpty(fileType)) {
            // 文件后缀
            try {
                fileType = FileTypeUtil.getType(file.getInputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return fileType;
    }

    /**
     * 获取文件全名
     */
    protected static String getFileType(File file) {
        // 文件扩展名
        String fileType = FileNameUtil.extName(file.getName());
        if (StringUtils.isEmpty(fileType)) {
            // 文件后缀
            fileType = FileTypeUtil.getType(file);
        }
        if (StringUtils.isEmpty(fileType)) {
            fileType = "txt";
        }
        return fileType;
    }

    /**
     * 获取文件流
     */
    public InputStream getInputStream(String urlPath) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(5000);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            // 从服务器返回一个输入流
            return httpURLConnection.getInputStream();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("获取文件流失败");
        }
    }

    /**
     * 封装对象
     */
    protected static UploadFileVo format(String fileName, String serverUrl, String fileKey, String fileType) {
        // 服务器地址
        UploadFileVo fileVo = new UploadFileVo()
                .setFileName(fileName)
                .setFullPath(serverUrl + FileNameUtil.UNIX_SEPARATOR + fileKey)
                .setFileType(fileType);
        return fileVo;
    }

    /**
     * 删除本地文件
     */
    public boolean delFile(File file) {
        try {
            return FileUtil.del(file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("删除上传失败");
        }
    }

}
