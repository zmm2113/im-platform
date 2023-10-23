package com.platform.common.version;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class VersionHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<VersionCondition> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createCondition(apiVersion);
    }

    // 实例化RequestCondition
    private RequestCondition<VersionCondition> createCondition(ApiVersion apiVersion) {
        if (apiVersion == null) {
            return null;
        }
        return new VersionCondition(apiVersion.value().getCode());
    }

}
