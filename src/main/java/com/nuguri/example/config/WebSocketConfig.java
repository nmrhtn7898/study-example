package com.nuguri.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

/**
 * 웹 소켓 커넥션 설정
 */
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 클라이언트가 웹 소켓 서버에 연결하는 엔드 포인트를 등록
     * STOMP => Simple Text Oriented Messaging Protocol, WebSocket 프로토콜을 통해 메시지 전달하는 프로토콜
     * withSockJs 웹 소켓을 지원하지 않는 브라우저에 폴백 옵션을 활성화
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    accessor.setUser(accessor::getLogin);
                }
                return message;
            }
        });
    }

    /**
     * 클라이언트 <=> 클라이언트 메시지 라우팅, 특정 주제를 구독한 모든 클라이언트에게 메시지 전달(broadcast)
     * BroadCast => 송신 호스트가 전송한 데이터가 네트워크에 연결된 모든 호스트에 전송되는 방식
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic", "/queue");
    }

}
