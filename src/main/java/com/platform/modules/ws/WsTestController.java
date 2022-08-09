package com.platform.modules.ws;

import cn.hutool.core.lang.Dict;
import com.platform.common.aspectj.IgnoreAuth;
import com.platform.common.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共服务请求处理
 */
@RestController
@RequestMapping("/ws/test")
@Slf4j
public class WsTestController {

    @Autowired
    private BootWebSocketHandler bootWebSocketHandler;

    /**
     * TODO 测试
     */
    @IgnoreAuth
    @GetMapping("/{userId}/{text}")
    public AjaxResult token(@PathVariable Long userId, @PathVariable String text) {
        Dict dict = Dict.create().set("msg", text);
        return AjaxResult.success(bootWebSocketHandler.sendMsg(userId, dict));
    }

}
