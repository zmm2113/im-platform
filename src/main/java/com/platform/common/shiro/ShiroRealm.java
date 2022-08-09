package com.platform.common.shiro;

import cn.hutool.core.util.RandomUtil;
import com.platform.common.enums.ResultCodeEnum;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.exception.LoginException;
import com.platform.common.shiro.utils.Md5Utils;
import com.platform.common.shiro.vo.LoginUser;
import com.platform.common.utils.IpUtils;
import com.platform.common.utils.ServletUtils;
import com.platform.modules.auth.service.TokenService;
import com.platform.modules.chat.domain.ChatUser;
import com.platform.modules.chat.service.ChatUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

/**
 * ShiroRealm
 */
public class ShiroRealm extends AuthorizingRealm {

    @Lazy
    @Resource
    private TokenService tokenService;

    @Lazy
    @Resource
    private ChatUserService chatUserService;

    /**
     * 提供用户信息，返回权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object object = ShiroUtils.getSubject().getPrincipal();
        if (object == null) {
            return null;
        }
        // 后台管理
        if (object instanceof LoginUser) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            return info;
        }
        return null;
    }

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof ShiroLoginToken
                || authenticationToken instanceof ShiroLoginAuth
                || authenticationToken instanceof ShiroLoginPhone
                || authenticationToken instanceof ShiroLoginThird;
    }

    /**
     * 身份认证/登录，验证用户是不是拥有相应的身份； 用于登陆认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // token
        if (authenticationToken instanceof ShiroLoginToken) {
            String token = (String) authenticationToken.getPrincipal();
            LoginUser loginUser = tokenService.queryByToken(token);
            if (loginUser == null) {
                throw new LoginException(ResultCodeEnum.UNAUTHORIZED);
            }
            // 加密盐
            String salt = Md5Utils.salt();
            // 对token加密
            String credentials = Md5Utils.credentials(token, salt);
            return new SimpleAuthenticationInfo(loginUser, credentials, ByteSource.Util.bytes(salt), getName());
        }
        // 手机+密码登录
        if (authenticationToken instanceof ShiroLoginAuth) {
            ShiroLoginAuth token = (ShiroLoginAuth) authenticationToken;
            return makeLoginUser(token.getPhone(), true);
        }
        // 手机+验证码登录
        if (authenticationToken instanceof ShiroLoginPhone) {
            ShiroLoginPhone token = (ShiroLoginPhone) authenticationToken;
            return makeLoginUser(token.getPhone(), false);
        }
        return null;
    }

    /**
     * 组装登录对象
     */
    private SimpleAuthenticationInfo makeLoginUser(String phone, boolean isPassword) {
        // 查询用户
        ChatUser chatUser = chatUserService.queryByPhone(phone);
        // 处理用户
        if (chatUser == null) {
            throw new AuthenticationException("手机号或密码不正确"); // 手机不存在
        }
        if (!YesOrNoEnum.YES.equals(chatUser.getStatus())) {
            throw new LoginException("手机号已停用"); // 手机禁用
        }
        // 查询权限
        LoginUser loginUser = new LoginUser()
                .setUserId(chatUser.getUserId())
                .setPhone(chatUser.getPhone())
                .setIpAddr(IpUtils.getIpAddr(ServletUtils.getRequest()))
                .setToken(RandomUtil.randomString(32));
        String credentials = chatUser.getPassword();
        String salt = chatUser.getSalt();
        if (!isPassword) {
            // 加密盐
            salt = Md5Utils.salt();
            // 对token加密
            credentials = Md5Utils.credentials(phone, salt);
        }
        // 登录
        return new SimpleAuthenticationInfo(loginUser, credentials, ByteSource.Util.bytes(salt), getName());
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        return super.isPermitted(principals, permission);
    }

}
