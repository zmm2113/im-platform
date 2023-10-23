package com.platform.common.version;

import java.lang.annotation.*;

/**
 * 自定义一个注解，给需要版本控制的方法加上该注解
 * <p>
 * 使用：@SubmitRepeat
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {

    /**
     * 版本号
     */
    VersionEnum value();

}
