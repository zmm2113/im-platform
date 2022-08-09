package com.platform.modules.push.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 大消息
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushBigVo {

    /**
     * 消息内容(消息Id)
     */
    private String content;

}
