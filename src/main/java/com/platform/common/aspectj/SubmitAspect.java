package com.platform.common.aspectj;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.utils.ServletUtils;
import com.platform.common.web.domain.AjaxResult;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Configuration
public class SubmitAspect {

    private final Cache<String, Object> CACHES = CacheBuilder.newBuilder()
            // 最大缓存 100 个
            .maximumSize(100)
            // 设置缓存过期时间为S
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    @SneakyThrows
    @Around("execution(* com.platform..*Controller.*(..)) && @annotation(submitRepeat)")
    public Object around(ProceedingJoinPoint joinPoint, SubmitRepeat submitRepeat) {
        String cacheKey = getCacheKey(submitRepeat);
        if (!StringUtils.isEmpty(cacheKey)) {
            if (CACHES.getIfPresent(cacheKey) == null) {
                // 如果是第一次请求,就将key存入缓存中
                CACHES.put(cacheKey, cacheKey);
            } else {
                return AjaxResult.fail("请勿重复请求");
            }
        }
        return joinPoint.proceed();
    }


    /**
     * 加上用户的唯一标识
     */
    private String getCacheKey(SubmitRepeat submitRepeat) {
        StringBuilder builder = new StringBuilder();
        HttpServletRequest request = ServletUtils.getRequest();
        String param = null;
        // 如果登录获取登录参数
        if (ShiroUtils.isLogin()) {
            param = ShiroUtils.getToken();
        }
        // 获取param参数
        if (StringUtils.isEmpty(param)) {
            Map<String, String> map = ServletUtil.getParamMap(request);
            param = JSONUtil.toJsonStr(map);
        }
        builder.append(param);
        builder.append("_");
        // 获取path参数
        String path = submitRepeat.path();
        if (StringUtils.isEmpty(path)) {
            path = request.getServletPath();
        }
        builder.append(path);
        return builder.toString();
    }

}
