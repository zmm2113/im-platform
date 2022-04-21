package com.platform.common.aspectj;

import com.platform.common.exception.BaseException;
import com.platform.common.aspectj.annotation.SubmitRepeat;
import com.platform.common.utils.ServletUtils;
import com.platform.common.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class SubmitAspect {

    private static final String DUPLICATE_KEY = "sys:submit:";

    @Autowired
    private RedisUtils redisUtils;

    @Around("execution(* com.platform..*Controller.*(..)) && @annotation(submitRepeat)")
    public Object around(ProceedingJoinPoint joinPoint, SubmitRepeat submitRepeat) {
        String cacheKey = getCacheKey();
        if (redisUtils.hasKey(cacheKey)) {
            throw new BaseException("请勿重复请求");
        }
        redisUtils.set(cacheKey, "0", submitRepeat.value(), TimeUnit.SECONDS);
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("日志错误：", throwable);
            throw new BaseException(throwable.getMessage());
        }
    }

    /**
     * 加上用户的唯一标识
     */
    private String getCacheKey() {
        StringBuilder builder = new StringBuilder(DUPLICATE_KEY);
        builder.append(ServletUtils.getSessionId());
        builder.append("_");
        builder.append(ServletUtils.getRequest().getServletPath());
        return builder.toString();
    }

}
