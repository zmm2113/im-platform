package com.platform.common.config;

import com.platform.modules.ws.BootWebSocketHandler;
import com.platform.modules.ws.BootWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/*
1、如果使用默认的嵌入式容器 比如Tomcat 则必须手工在上下文提供ServerEndpointExporter。
2、如果使用外部容器部署war包，则不需要提供提供ServerEndpointExporter，因为此时SpringBoot默认将扫描 服务端的行为交给外部容器处理，所以线上部署的时候要把WebSocketConfig中这段注入bean的代码注掉
*/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private BootWebSocketHandler bootWebSocketHandler;

    @Autowired
    private BootWebSocketInterceptor bootWebSocketInterceptor;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(bootWebSocketHandler, "/ws")
                .addInterceptors(bootWebSocketInterceptor)
                .setAllowedOrigins("*");
    }

}
