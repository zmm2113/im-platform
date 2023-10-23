package com.platform.common.version;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.HeadConstant;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.web.domain.AjaxResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

/**
 * 签名拦截器
 */
@Component
public class SignInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (YesOrNoEnum.NO.equals(VersionConfig.ENABLED)) {
            return true;
        }
        String currentUrl = request.getServletPath();
        if (VersionUtils.verifyUrl(currentUrl, VersionConfig.EXCLUDES)) {
            return true;
        }
        String sign = ServletUtil.getHeader(request, HeadConstant.SIGN, CharsetUtil.UTF_8);
        String timestamp = ServletUtil.getHeader(request, HeadConstant.TIMESTAMP, CharsetUtil.UTF_8);
        String appId = ServletUtil.getHeader(request, HeadConstant.APP_ID, CharsetUtil.UTF_8);
        if (StringUtils.isEmpty(sign)
                || StringUtils.isEmpty(appId)
                || StringUtils.isEmpty(timestamp)) {
            return error(response);
        }
        String appSecret = VersionConfig.SIGN.get(appId);
        if (appSecret == null) {
            return error(response);
        }
        if (!NumberUtil.isLong(timestamp)) {
            return error(response);
        }
        Date date = DateUtil.date(NumberUtil.parseLong(timestamp));
        if (DateUtil.between(date, DateUtil.date(), DateUnit.MINUTE) > 5) {
            return error(response);
        }
        String path = request.getRequestURI();
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String query = request.getQueryString();
            if (!StringUtils.isEmpty(query)) {
                path += "?" + URLDecoder.decode(query, "UTF-8");
            }
        }
        String param = SecureUtil.md5(appId + path + timestamp);
        // 此处密钥如果有非ASCII字符，考虑编码
        String result = SecureUtil.hmacMd5(appSecret).digestHex(param);
        if (!sign.equalsIgnoreCase(result)) {
            return error(response);
        }
        return true;
    }

    private boolean error(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSONUtil.toJsonStr(AjaxResult.fail("请求不正确")));
        return false;
    }

}
