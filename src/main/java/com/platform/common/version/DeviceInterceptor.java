package com.platform.common.version;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.HeadConstant;
import com.platform.common.core.EnumUtils;
import com.platform.common.enums.DeviceEnum;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.web.domain.AjaxResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 设备拦截器
 */
@Component
public class DeviceInterceptor implements HandlerInterceptor {

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
        String device = ServletUtil.getHeader(request, HeadConstant.DEVICE, CharsetUtil.UTF_8);
        if (StringUtils.isEmpty(device)) {
            return error(response);
        }
        DeviceEnum deviceEnum = EnumUtils.toEnum(DeviceEnum.class, device);
        if (deviceEnum == null) {
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
