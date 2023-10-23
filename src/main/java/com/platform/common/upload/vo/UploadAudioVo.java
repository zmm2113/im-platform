package com.platform.common.upload.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件上传
 */
@Data
@Accessors(chain = true) // 链式调用
public class UploadAudioVo extends UploadFileVo {

    /**
     * 识别文字
     */
    private String sourceText;

}
