package com.platform.common.aspectj;

import java.lang.annotation.*;

/**
 * 忽略登录
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented //可生成文档
public @interface IgnoreAuth {

}