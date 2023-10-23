package com.platform.modules.chat.service;

import com.platform.common.web.service.BaseService;
import com.platform.modules.chat.domain.ChatMsg;
import com.platform.modules.chat.vo.ChatVo01;
import com.platform.modules.chat.vo.ChatVo02;
import com.platform.modules.chat.vo.ChatVo03;

/**
 * <p>
 * 聊天消息 服务层
 * q3z3
 * </p>
 */
public interface ChatMsgService extends BaseService<ChatMsg> {

    /**
     * 发送消息
     */
    ChatVo03 sendFriendMsg(ChatVo01 chatVo);

    /**
     * 发送群消息
     */
    ChatVo03 sendGroupMsg(ChatVo02 chatVo);

}
