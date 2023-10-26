package com.platform.modules.push.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.constant.AppConstants;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.redis.RedisUtils;
import com.platform.modules.push.enums.PushBodyEnum;
import com.platform.modules.push.enums.PushMsgEnum;
import com.platform.modules.push.enums.PushNoticeEnum;
import com.platform.modules.push.service.ChatPushService;
import com.platform.modules.push.vo.*;
import com.platform.modules.ws.BootWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
    private BootWebSocketHandler bootWebSocketHandler;

    @Override
    public void pushMsg(PushParamVo from, PushMsgEnum msgType) {
        this.doMsg(from, null, msgType);
    }

    @Override
    public void pushMsg(List<PushParamVo> userList, PushMsgEnum msgType) {
        userList.forEach(e -> {
            this.doMsg(e, e, msgType);
        });
    }

    @Override
    public void pushGroupMsg(PushParamVo from, PushParamVo group, PushMsgEnum msgType) {
        this.doMsg(from, group, msgType);
    }

    @Override
    public void pushGroupMsg(List<PushParamVo> userList, PushParamVo group, PushMsgEnum msgType) {
        userList.forEach(e -> {
            this.doMsg(e, group, msgType);
        });
    }

    /**
     * 发送消息
     */
    private void doMsg(PushParamVo from, PushParamVo group, PushMsgEnum msgType) {
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
        if (msgId == null) {
            msgId = IdWorker.getId();
        }
        PushBodyVo pushBodyVo = new PushBodyVo(msgId, PushBodyEnum.MSG, pushMsgVo);
        // 发送人
        pushBodyVo.setFromInfo(BeanUtil.toBean(from, PushFromVo.class).setUserType(from.getUserType().getCode()));
        // 接收人
        if (group != null) {
            pushBodyVo.setGroupInfo(BeanUtil.toBean(group, PushToVo.class));
        }
        // 发送消息
        this.push(userId, pushBodyVo);
    }

    @Override
    public void pullMsg(Long userId) {
        String redisKey = StrUtil.format(AppConstants.REDIS_MSG, userId, "*");
        Set<String> redisKeys = this.redisUtils.keys(redisKey);
        if (CollectionUtils.isEmpty(redisKeys)) {
            return;
        }
        // 获取消息
        List<String> redisValues = this.redisUtils.multiGet(CollUtil.sort(redisKeys, Comparator.naturalOrder()));
        // 循环发送
        redisValues.forEach(content -> {
            // 发送消息
            bootWebSocketHandler.sendMsg(userId, content);
        });
        // 移除离线消息
        redisUtils.delete(redisKeys);
    }

    @Override
    public void pushNotice(PushParamVo paramVo, PushNoticeEnum pushNotice) {
        this.pushNotice(Arrays.asList(paramVo), pushNotice);
    }

    @Override
    public void pushNotice(List<PushParamVo> userList, PushNoticeEnum pushNotice) {
        userList.forEach(e -> {
            this.doNotice(e.getToId(), e, pushNotice);
        });
    }

    /**
     * 发送通知
     */
    private void doNotice(Long userId, PushParamVo paramVo, PushNoticeEnum pushNotice) {
        // 组装消息体
        PushNoticeVo pushNoticeVo = new PushNoticeVo();
        switch (pushNotice) {
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
        Long msgId = IdWorker.getId();
        PushBodyVo pushBodyVo = new PushBodyVo(msgId, PushBodyEnum.NOTICE, pushNoticeVo);
        // 发送消息
        this.push(userId, pushBodyVo);
    }

    /**
     * 推送
     */
    private void push(Long userId, PushBodyVo pushBodyVo) {
        // 组装消息
        String content = JSONUtil.toJsonStr(pushBodyVo);
        // 发送消息
        bootWebSocketHandler.sendMsg(userId, content);
        // 离线消息
        String redisKey = StrUtil.format(AppConstants.REDIS_MSG, userId, pushBodyVo.getMsgId());
        redisUtils.set(redisKey, content, AppConstants.REDIS_MSG_TIME, TimeUnit.DAYS);
    }

}
