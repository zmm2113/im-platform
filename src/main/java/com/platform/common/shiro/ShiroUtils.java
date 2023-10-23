
package com.platform.common.shiro;

import com.platform.common.constant.HeadConstant;
import com.platform.common.utils.ServletUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Shiro工具类
 */
public class ShiroUtils {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static LoginUser getLoginUser() {
        return (LoginUser) getSubject().getPrincipal();
    }

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }

    public static String getToken() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getToken();
        }
        return ServletUtils.getRequest().getHeader(HeadConstant.TOKEN_HEADER_ADMIN);
    }

    public static String getPhone() {
        return getLoginUser().getPhone();
    }

    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

}
