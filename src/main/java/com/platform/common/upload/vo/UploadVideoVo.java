package com.platform.common.upload.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件上传
 */
@Data
@Accessors(chain = true) // 链式调用
public class UploadVideoVo {

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件地址
     */
    private String fullPath;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件封面
     */
    private String screenShot;

}
