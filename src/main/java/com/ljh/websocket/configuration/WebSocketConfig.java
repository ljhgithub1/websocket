package com.ljh.websocket.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Liu, jihong
 * @version 1.0
 * @date 2019-09-12 上午 10:54
 */

/**
 * 将ServerEndpointExporter对象注入到spring容器中
 * 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
 *
 * @ServerEndpoint：就相当于requestmapping，接受客服端发过来的请求，进行映射
 */

/**
 * ssss
 */
@Configuration
public class WebSocketConfig {


    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
