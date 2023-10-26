package com.platform.modules.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.platform.common.constant.AppConstants;
import com.platform.common.constant.HeadConstant;
import com.platform.common.enums.GenderEnum;
import com.platform.common.exception.BaseException;
import com.platform.common.exception.LoginException;
import com.platform.common.redis.GeoHashUtils;
import com.platform.common.redis.RedisUtils;
import com.platform.common.shiro.Md5Utils;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.utils.ServletUtils;
import com.platform.common.web.service.impl.BaseServiceImpl;
import com.platform.modules.auth.service.TokenService;
import com.platform.modules.auth.vo.AuthVo01;
import com.platform.modules.chat.dao.ChatUserDao;
import com.platform.modules.chat.domain.ChatUser;
import com.platform.modules.chat.service.ChatUserService;
import com.platform.modules.chat.vo.MyVo09;
import com.platform.modules.push.service.ChatPushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户表 服务层实现
 * q3z3
 * </p>
 */
@Slf4j
@Service("chatUserService")
public class ChatUserServiceImpl extends BaseServiceImpl<ChatUser> implements ChatUserService {

    @Resource
    private ChatUserDao chatUserDao;

    @Resource
    private TokenService tokenService;

    @Resource
    private ChatPushService chatPushService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private GeoHashUtils geoHashUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatUserDao);
    }

    @Override
    public List<ChatUser> queryList(ChatUser t) {
        List<ChatUser> dataList = chatUserDao.queryList(t);
        return dataList;
    }

    @Override
    public void register(AuthVo01 authVo) {
        String phone = authVo.getPhone();
        String password = authVo.getPassword();
        String nickName = authVo.getNickName();
        String msg = "此手机号码已注册过，请勿重复注册";
        // 验证手机号是否注册过
        if (this.queryCount(new ChatUser().setPhone(phone)) > 0) {
            throw new BaseException(msg);
        }
        String salt = RandomUtil.randomString(4);
        String chatNo = IdUtil.simpleUUID();
        ChatUser cu = new ChatUser()
                .setNickName(nickName)
                .setChatNo(chatNo)
                .setGender(GenderEnum.MALE)
                .setPortrait(AppConstants.DEFAULT_PORTRAIT)
                .setSalt(salt)
                .setPhone(phone)
                .setPassword(Md5Utils.credentials(password, salt))
                .setCreateTime(DateUtil.date());
        try {
            this.add(cu);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BaseException(msg);
        }
    }

    @Override
    public ChatUser queryByPhone(String phone) {
        return this.queryOne(new ChatUser().setPhone(phone));
    }

    @Override
    public void resetPass(Long userId, String password) {
        String salt = RandomUtil.randomString(4);
        ChatUser chatUser = new ChatUser()
                .setUserId(userId)
                .setSalt(salt)
                .setPassword(Md5Utils.credentials(password, salt));
        this.updateById(chatUser);
    }

    @Override
    public void editPass(String password, String pwd) {
        // 当前用户
        ChatUser cu = getById(ShiroUtils.getUserId());
        if (!Md5Utils.credentials(password, cu.getSalt()).equalsIgnoreCase(cu.getPassword())) {
            throw new BaseException("旧密码不正确");
        }
        String salt = RandomUtil.randomString(4);
        ChatUser chatUser = new ChatUser()
                .setUserId(cu.getUserId())
                .setSalt(salt)
                .setPassword(Md5Utils.credentials(pwd, salt));
        this.updateById(chatUser);
    }

    @Override
    public void editChatNo(String chatNo) {
        Long userId = ShiroUtils.getUserId();
        String errMsg = "微聊号已被占用，请重新输入";
        // 校验
        ChatUser cu = this.queryOne(new ChatUser().setChatNo(chatNo));
        if (cu != null && !userId.equals(cu.getUserId())) {
            throw new BaseException(errMsg);
        }
        try {
            // 更新
            this.updateById(new ChatUser().setUserId(userId).setChatNo(chatNo));
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BaseException(errMsg);
        }

    }

    @Override
    public MyVo09 getInfo() {
        // 当前用户
        ChatUser cu = findById(ShiroUtils.getUserId());
        return BeanUtil.toBean(cu, MyVo09.class)
                .setPhone(DesensitizedUtil.mobilePhone(cu.getPhone()));
    }

    @Override
    public String getQrCode() {
        Long userId = ShiroUtils.getUserId();
        return AppConstants.QR_CODE_USER + userId;
    }

    @Transactional
    @Override
    public void deleted() {
        // 移除缓存
        removeCache();
        // 更新用户
        ChatUser cu = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setDeletedTime(DateUtil.date());
        this.updateById(cu);
    }

    @Transactional
    @Override
    public Dict doLogin(AuthenticationToken authenticationToken) {
        String msg = null;
        try {
            ShiroUtils.getSubject().login(authenticationToken);
        } catch (LoginException e) {
            msg = e.getMessage();
        } catch (AuthenticationException e) {
            msg = "手机号或密码不正确";
        } catch (Exception e) {
            msg = "未知异常";
            log.error(msg, e);
        }
        if (!StringUtils.isEmpty(msg)) {
            throw new BaseException(msg);
        }
        Long userId = ShiroUtils.getUserId();
        tokenService.deleteToken(this.getById(userId).getToken());
        // 生成新TOKEN
        String token = tokenService.generateToken();
        String version = ServletUtils.getRequest().getHeader(HeadConstant.VERSION);
        ChatUser chatUser = new ChatUser()
                .setUserId(userId)
                .setToken(token)
                .setVersion(version);
        // 更新token
        this.updateById(chatUser);
        return Dict.create().set("token", token);
    }

    @Override
    public void logout() {
        try {
            // 移除缓存
            this.removeCache();
            // 执行退出
            ShiroUtils.getSubject().logout();
            log.info("退出成功。。。。");
        } catch (Exception ex) {
            log.error("退出异常", ex);
        }
    }

    @Override
    public void refresh() {
        Long userId = ShiroUtils.getUserId();
        // 拉取离线消息
        chatPushService.pullMsg(userId);
    }

    /**
     * 移除缓存
     */
    private void removeCache() {
        Long userId = ShiroUtils.getUserId();
        ChatUser chatUser = this.getById(userId);
        if (chatUser != null) {
            // 清理token
            tokenService.deleteToken(chatUser.getToken());
        }
        String userStr = NumberUtil.toStr(userId);
        // 附近的人
        geoHashUtils.remove(AppConstants.REDIS_NEAR, userStr);
        // 摇一摇
        redisUtils.lRemove(AppConstants.REDIS_SHAKE, 0, userStr);
    }

}
