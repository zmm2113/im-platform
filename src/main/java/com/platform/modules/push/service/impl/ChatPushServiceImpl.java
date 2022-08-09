package com.platform.modules.push.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.AppConstants;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.utils.SnowflakeUtils;
import com.platform.common.utils.redis.RedisUtils;
import com.platform.modules.push.config.PushConfig;
import com.platform.modules.push.dto.PushMsgDto;
import com.platform.modules.push.dto.PushTokenDto;
import com.platform.modules.push.enums.PushBodyTypeEnum;
import com.platform.modules.push.enums.PushMsgTypeEnum;
import com.platform.modules.push.enums.PushNoticeTypeEnum;
import com.platform.modules.push.service.ChatPushService;
import com.platform.modules.push.utils.PushUtils;
import com.platform.modules.push.vo.*;
import com.platform.modules.ws.BootWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户推送 服务层
 * q3z3
 * </p>
 */
@Service("chatPushService")
@Slf4j
public class ChatPushServiceImpl implements ChatPushService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PushConfig pushConfig;

    @Autowired
    private BootWebSocketHandler bootWebSocketHandler;

    /**
     * 消息长度
     */
    private static final Integer MSG_LENGTH = 2048;

    @Override
    public void setAlias(Long userId, String cid) {
        // 异步注册
        PushTokenDto pushTokenDto = initPushToken();
        ThreadUtil.execAsync(() -> {
            PushAliasVo aliasVo = new PushAliasVo()
                    .setCid(cid)
                    .setAlias(NumberUtil.toStr(userId));
            PushUtils.setAlias(pushTokenDto, aliasVo);
        });
    }

    @Override
    public void delAlias(Long userId, String cid) {
        // 异步注册
        PushTokenDto pushTokenDto = initPushToken();
        ThreadUtil.execAsync(() -> {
            PushAliasVo aliasVo = new PushAliasVo()
                    .setCid(cid)
                    .setAlias(NumberUtil.toStr(userId));
            PushUtils.delAlias(pushTokenDto, aliasVo);
        });
    }

    @Override
    public void pushMsg(PushParamVo from, PushMsgTypeEnum msgType) {
        PushTokenDto pushTokenDto = initPushToken();
        // 异步发送
        ThreadUtil.execAsync(() -> {
            doMsg(from, null, msgType, pushTokenDto);
        });
    }

    @Override
    public void pushMsg(List<PushParamVo> userList, PushMsgTypeEnum msgType) {
        PushTokenDto pushTokenDto = initPushToken();
        // 异步发送
        ThreadUtil.execAsync(() -> {
            userList.forEach(e -> {
                doMsg(e, e, msgType, pushTokenDto);
            });
        });
    }

    @Override
    public void pushMsg(List<PushParamVo> userList, PushParamVo group, PushMsgTypeEnum msgType) {
        PushTokenDto pushTokenDto = initPushToken();
        // 异步发送
        ThreadUtil.execAsync(() -> {
            userList.forEach(e -> {
                doMsg(e, group, msgType, pushTokenDto);
            });
        });
    }

    /**
     * 发送消息
     */
    private void doMsg(PushParamVo from, PushParamVo to, PushMsgTypeEnum msgType, PushTokenDto pushTokenDto) {
        Long userId = from.getToId();
        // 组装消息体
        PushMsgVo pushMsgVo = new PushMsgVo()
                .setMsgType(msgType.getCode())
                .setContent(from.getContent());
        YesOrNoEnum top = from.getTop();
        if (top != null) {
            pushMsgVo.setTop(top.getCode());
        }
        YesOrNoEnum disturb = from.getDisturb();
        if (disturb != null) {
            pushMsgVo.setDisturb(disturb.getCode());
        }
        Long msgId = from.getMsgId();
        PushBodyVo pushBodyVo = new PushBodyVo(msgId, PushBodyTypeEnum.MSG, pushMsgVo);
        // 发送人
        pushBodyVo.setFromInfo(BeanUtil.toBean(from, PushFromVo.class).setUserType(from.getUserType().getCode()));
        // 接收人
        if (to != null) {
            pushBodyVo.setGroupInfo(BeanUtil.toBean(to, PushToVo.class));
        }
        PushMsgDto pushMsgDto = initTransmission(pushBodyVo);
        // 验证消息长度
        if (StrUtil.length(from.getContent()) > MSG_LENGTH) {
            // 组装消息体
            PushMsgDto pushBigDto = initTransmission(new PushBodyVo(msgId, PushBodyTypeEnum.BIG, new PushBigVo().setContent(String.valueOf(msgId))));
            // 发送消息
            push(userId, pushBigDto, pushTokenDto);
            // 存离线消息
            String key = AppConstants.REDIS_MSG_BIG + msgId;
            redisUtils.set(key, JSONUtil.toJsonStr(pushBodyVo), AppConstants.REDIS_MSG_TIME, TimeUnit.DAYS);
            return;
        }
        // 发送消息
        push(userId, pushMsgDto, pushTokenDto);
    }

    @Override
    public void pullOffLine(Long userId) {
        // 异步执行
        ThreadUtil.execAsync(() -> {
            String key = makeMsgKey(userId);
            Long size = redisUtils.lLen(key);
            if (size.longValue() == 0) {
                return;
            }
            PushTokenDto pushTokenDto = initPushToken();
            for (int i = 0; i < size; i++) {
                String json = redisUtils.lLeftPop(key);
                PushMsgDto pushMsgDto = JSONUtil.toBean(json, PushMsgDto.class);
                // 发送消息
                push(userId, pushMsgDto, pushTokenDto);
            }
            redisUtils.delete(key);
        });
    }

    @Override
    public void pushNotice(PushParamVo paramVo, PushNoticeTypeEnum pushNoticeType) {
        this.pushNotice(Arrays.asList(paramVo), pushNoticeType);
    }

    @Override
    public void pushNotice(List<PushParamVo> userList, PushNoticeTypeEnum pushNoticeType) {
        PushTokenDto pushTokenDto = initPushToken();
        // 异步发送
        ThreadUtil.execAsync(() -> {
            userList.forEach(e -> {
                this.doNotice(e.getToId(), e, pushTokenDto, pushNoticeType);
            });
        });
    }

    /**
     * 发送通知
     */
    private void doNotice(Long userId, PushParamVo paramVo, PushTokenDto pushTokenDto, PushNoticeTypeEnum pushNoticeType) {
        // 组装消息体
        PushNoticeVo pushNoticeVo = new PushNoticeVo();
        switch (pushNoticeType) {
            case TOPIC_RED:
                pushNoticeVo.setTopicRed(Dict.create().set("portrait", paramVo.getPortrait()));
                break;
            case TOPIC_REPLY:
                Long topicCount = redisUtils.increment(AppConstants.REDIS_TOPIC_NOTICE + userId, 1);
                pushNoticeVo.setTopicReply(Dict.create().set("count", topicCount).set("portrait", paramVo.getPortrait()));
                break;
            case FRIEND_APPLY:
                Long applyCount = redisUtils.increment(AppConstants.REDIS_FRIEND_NOTICE + userId, 1);
                pushNoticeVo.setFriendApply(Dict.create().set("count", applyCount));
                break;
        }
        Long msgId = SnowflakeUtils.getNextId();
        PushBodyVo pushBodyVo = new PushBodyVo(msgId, PushBodyTypeEnum.NOTICE, pushNoticeVo);
        PushMsgDto pushMsgDto = initTransmission(pushBodyVo);
        // 发送消息
        push(userId, pushMsgDto, pushTokenDto);
    }

    /**
     * 存储离线消息
     */
    private void setOffLineMsg(Long userId, PushMsgDto pushMsgDto) {
        String key = makeMsgKey(userId);
        redisUtils.lRightPush(key, JSONUtil.toJsonStr(pushMsgDto));
        redisUtils.expire(key, AppConstants.REDIS_MSG_TIME, TimeUnit.DAYS);
    }

    /**
     * 组装消息前缀
     */
    private String makeMsgKey(Long userId) {
        return AppConstants.REDIS_MSG + userId;
    }

    /**
     * 组装透传消息
     */
    private PushMsgDto initTransmission(PushBodyVo pushBodyVo) {
        return new PushMsgDto().setTransmission(Dict.create().parseBean(pushBodyVo));
    }

    /**
     * 初始化token
     */
    private PushTokenDto initPushToken() {
        String key = AppConstants.REDIS_PUSH_TOKEN + pushConfig.getAppId();
        PushTokenDto pushTokenDto;
        if (redisUtils.hasKey(key)) {
            String json = redisUtils.get(key);
            pushTokenDto = JSONUtil.toBean(json, PushTokenDto.class);
        } else {
            pushTokenDto = PushUtils.createToken(pushConfig);
            redisUtils.set(key, JSONUtil.toJsonStr(pushTokenDto), 1, TimeUnit.HOURS);
        }
        return pushTokenDto;
    }

    /**
     * 推送
     */
    private void push(Long userId, PushMsgDto pushMsgDto, PushTokenDto pushTokenDto) {
        // 发送推送消息
        PushResultVo pushResult1 = PushUtils.pushAlias(userId, pushMsgDto, pushTokenDto);
        // 发送ws消息
        PushResultVo pushResult2 = bootWebSocketHandler.sendMsg(userId, pushMsgDto.getTransmission());
        if (pushResult1.isResult() && pushResult1.isOnline()) {
            return;
        }
        if (pushResult2.isResult() && pushResult2.isOnline()) {
            return;
        }
        // 设置离线消息
        setOffLineMsg(userId, pushMsgDto);
    }

}
