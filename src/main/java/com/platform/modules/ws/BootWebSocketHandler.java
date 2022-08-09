package com.platform.modules.ws;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.AppConstants;
import com.platform.common.enums.ResultCodeEnum;
import com.platform.common.exception.BaseException;
import com.platform.modules.push.service.ChatPushService;
import com.platform.modules.push.vo.PushResultVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BootWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<Long, List<WebSocketSession>> POOL_SESSION = new ConcurrentHashMap<>();

    @Resource
    private ChatPushService chatPushService;

    /**
     * socket 建立成功事件
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        if (userId == null) {
            throw new BaseException(ResultCodeEnum.UNAUTHORIZED);
        }
        List<WebSocketSession> sessionList = POOL_SESSION.get(userId);
        if (CollectionUtils.isEmpty(sessionList)) {
            POOL_SESSION.put(userId, sessionList = new ArrayList<>());
        }
        sessionList.add(session);
        // 拉取离线消息
        chatPushService.pullOffLine(userId);
    }

    /**
     * 接收消息事件
     */
    @SneakyThrows
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        log.info("server 接收到消息 {}", payload);
        session.sendMessage(new TextMessage("ok"));
    }

    /**
     * socket 断开连接时
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        if (userId == null) {
            throw new BaseException(ResultCodeEnum.UNAUTHORIZED);
        }
        List<WebSocketSession> sessionList = POOL_SESSION.get(userId);
        if (CollectionUtils.isEmpty(sessionList)) {
            return;
        }
        sessionList.remove(session);
    }

    /**
     * 给某个用户发送消息
     */
    public PushResultVo sendMsg(Long userId, Dict transmission) {
        List<WebSocketSession> sessionList = POOL_SESSION.get(userId);
        if (CollectionUtils.isEmpty(sessionList)) {
            return PushResultVo.fail();
        }
        int result = 0;
        for (WebSocketSession session : sessionList) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(JSONUtil.toJsonStr(transmission)));
                    result++;
                }
            } catch (IOException e) {
                log.error("发送消息给{}失败", userId, e);
            }
        }
        if (result == 0) {
            return PushResultVo.fail();
        }
        return PushResultVo.success();
    }

}