package com.platform.modules.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthVo02 {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}
