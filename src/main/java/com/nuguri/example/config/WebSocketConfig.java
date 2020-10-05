package com.nuguri.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 웹 소켓 커넥션 설정
 */
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 클라이언트가 웹 소켓 서버에 연결하는 엔드 포인트를 등록
     * STOMP => Simple Text Oriented Messaging Protocol, WebSocket 프로토콜을 통해 메시지 전달하는 프로토콜
     * withSockJs 웹 소켓을 지원하지 않는 브라우저에 폴백 옵션을 활성화
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    /**
     * 클라이언트 <=> 클라이언트 메시지 라우팅, 특정 주제를 구독한 모든 클라이언트에게 메시지 전달(broadcast)
     * BroadCast => 송신 호스트가 전송한 데이터가 네트워크에 연결된 모든 호스트에 전송되는 방식
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic");
    }

}
