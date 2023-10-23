/**
 * Licensed to the Apache Software Foundation （ASF） under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * （the "License"）； you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * https://www.q3z3.com
 * QQ : 939313737
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.platform.modules.auth.service.impl;

import cn.hutool.json.JSONUtil;
import com.platform.common.config.PlatformConfig;
import com.platform.common.constant.HeadConstant;
import com.platform.common.redis.RedisUtils;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.shiro.LoginUser;
import com.platform.modules.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * token 服务层
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String generateToken() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        String token = loginUser.getToken();
        // 存储redis
        redisUtils.set(makeToken(token), JSONUtil.toJsonStr(loginUser), PlatformConfig.TIMEOUT, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public LoginUser queryByToken(String token) {
        String key = makeToken(token);
        if (!redisUtils.hasKey(key)) {
            return null;
        }
        // 续期
        redisUtils.expire(key, PlatformConfig.TIMEOUT, TimeUnit.MINUTES);
        // 转换
        return JSONUtil.toBean(redisUtils.get(key), LoginUser.class);
    }

    @Override
    public void deleteToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return;
        }
        redisUtils.delete(makeToken(token));
    }

    private String makeToken(String token) {
        return HeadConstant.TOKEN_REDIS_APP + token;
    }

}
