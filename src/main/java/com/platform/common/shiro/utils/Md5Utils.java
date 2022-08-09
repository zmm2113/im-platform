package com.platform.common.shiro.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

/**
 * Md5Util
 */
public class Md5Utils {

    /**
     * credentials
     */
    public static final String credentials(String password, String salt) {
        //盐：为了即使相同的密码不同的盐加密后的结果也不同
        ByteSource byteSalt = ByteSource.Util.bytes(salt);
        Md5Hash result = new Md5Hash(password, byteSalt);
        return result.toString();
    }

    /**
     * md5
     */
    public static final String md5(String str) {
        return SecureUtil.md5(str);
    }

    /**
     * 加密盐
     */
    public static String salt() {
        return RandomUtil.randomString(4);
    }

    /**
     * 基础密码
     */
    private static final String baseStr = "abcdefghgkmnprstwxyz123456789";

    /**
     * 密码
     */
    public static String password() {
        return RandomUtil.randomString(baseStr, 8);
    }

}
