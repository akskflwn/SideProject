package com.test.project.websocket.stomp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChatConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // HandShake와 통신을 담당할 EndPoint를 지정한다.
        // 클라이언트에서 서버로 WebSocket 연결을 하고 싶을 때, "/ws/chat "으로 요청을 보내도록 하였다.
        registry.addEndpoint("/ws/chat").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //Message broker 는 "/topic, "/queue "로 시작하는 주소의 Subscriber들에게 메시지를 전달하는 역할을 한다
        registry.enableSimpleBroker("/queue", "/topic");

        //클라이언트가 서버로 메시지 보낼 때 붙여야 하는 prefix를 지정한다.
        registry.setApplicationDestinationPrefixes("/app");
    }


}









