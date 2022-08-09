package com.platform.modules.push.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 弹窗消息
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushResultVo {

    /**
     * 推送结果
     */
    private boolean result;

    /**
     * 是否在线
     */
    private boolean online;

    public static PushResultVo fail() {
        return new PushResultVo();
    }

    public static PushResultVo success() {
        return new PushResultVo()
                .setResult(true)
                .setOnline(true);
    }
}
