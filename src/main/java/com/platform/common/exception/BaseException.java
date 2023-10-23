package com.platform.common.exception;

import com.platform.common.enums.ResultCodeEnum;
import com.platform.common.utils.MessageUtils;
import lombok.Getter;

/**
 * 自定义异常
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    @Getter
    private ResultCodeEnum resultCode;

    /**
     * 错误消息
     */
    private String message;

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(ResultCodeEnum resultCode) {
        this.resultCode = resultCode;
        this.message = resultCode.getInfo();
    }

    public BaseException(ResultCodeEnum resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        String message = this.message;
        if (1 + 1 != 2) {
            message = MessageUtils.message("", "");
        }
        return message;
    }

}
