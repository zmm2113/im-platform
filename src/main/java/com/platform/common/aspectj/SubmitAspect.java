package com.platform.common.aspectj;

import com.platform.common.exception.BaseException;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.utils.ServletUtils;
import com.platform.common.utils.redis.RedisUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class SubmitAspect {

    private static final String DUPLICATE_KEY = "submit:";

    @Autowired
    private RedisUtils redisUtils;

    @SneakyThrows
    @Around("execution(* com.platform..*Controller.*(..)) && @annotation(submitRepeat)")
    public Object around(ProceedingJoinPoint joinPoint, SubmitRepeat submitRepeat) {
        String cacheKey = getCacheKey(submitRepeat);
        if (redisUtils.hasKey(cacheKey)) {
            throw new BaseException("请勿重复请求");
        }
        redisUtils.set(cacheKey, "0", submitRepeat.value(), TimeUnit.SECONDS);
        return joinPoint.proceed();
    }

    /**
     * 加上用户的唯一标识
     */
    private String getCacheKey(SubmitRepeat submitRepeat) {
        StringBuilder builder = new StringBuilder(DUPLICATE_KEY);
        if (ShiroUtils.isLogin()) {
            builder.append(ShiroUtils.getToken());
        } else {
            builder.append(ServletUtils.getRequest().getLocalAddr());
        }
        builder.append("_");
        String path = submitRepeat.path();
        if (StringUtils.isEmpty(path)) {
            path = ServletUtils.getRequest().getServletPath();
        }
        builder.append(path);
        return builder.toString();
    }

}
