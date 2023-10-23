package com.platform.common.web.domain;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.common.core.EnumUtils;
import com.platform.common.enums.ResultCodeEnum;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * 操作消息提醒
 */
public class AjaxResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 AjaxResult 对象
     */
    public AjaxResult(ResultCodeEnum resultCode, String msg, Object data) {
        super.put(CODE_TAG, resultCode.getCode());
        super.put(MSG_TAG, StringUtils.isEmpty(msg) ? resultCode.getInfo() : msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     */
    public static AjaxResult success() {
        return new AjaxResult(ResultCodeEnum.SUCCESS, null, null);
    }

    /**
     * 返回成功数据
     */
    public static AjaxResult success(Object data) {
        return new AjaxResult(ResultCodeEnum.SUCCESS, null, data);
    }

    /**
     * 返回成功消息
     */
    public static AjaxResult successMsg(String msg) {
        return new AjaxResult(ResultCodeEnum.SUCCESS, msg, null);
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult fail() {
        return new AjaxResult(ResultCodeEnum.FAIL, null, null);
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult fail(String msg) {
        return new AjaxResult(ResultCodeEnum.FAIL, msg, null);
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult result(ResultCodeEnum resultCode) {
        return new AjaxResult(resultCode, resultCode.getInfo(), null);
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult result(ResultCodeEnum resultCode, String msg) {
        if (StringUtils.isEmpty(msg)) {
            msg = resultCode.getInfo();
        }
        return new AjaxResult(resultCode, msg, null);
    }

    @Override
    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public ResultCodeEnum getCode() {
        Object code = this.get(CODE_TAG);
        return EnumUtils.toEnum(ResultCodeEnum.class, code.toString(), ResultCodeEnum.FAIL);
    }

    public Object getData() {
        return this.get(DATA_TAG);
    }

    public String getDataStr() {
        Object data = getData();
        return StrUtil.str(data, Charset.defaultCharset());
    }

    public static AjaxResult ajax(String json) {
        if (StringUtils.isEmpty(json)) {
            return AjaxResult.fail();
        }
        JSONObject jsonObject = JSONUtil.parseObj(json);
        String code = StrUtil.str(jsonObject.get(CODE_TAG), Charset.defaultCharset());
        String msg = StrUtil.str(jsonObject.get(MSG_TAG), Charset.defaultCharset());
        ResultCodeEnum resultCode = EnumUtils.toEnum(ResultCodeEnum.class, code, ResultCodeEnum.FAIL);
        return new AjaxResult(resultCode, msg, jsonObject.get(DATA_TAG));
    }

}
