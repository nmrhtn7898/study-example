package com.nuguri.example.config;

import com.nuguri.example.entity.Account;
import com.nuguri.example.model.AccountAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * 웹 소켓 커넥션 설정
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final EntityManager entityManager;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WebSocketPrincipalResolver());
    }

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
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
                    Account account = ((AccountAdapter) ((UsernamePasswordAuthenticationToken) headerAccessor
                            .getUser())
                            .getPrincipal())
                            .getAccount();
                    String destination = new AntPathMatcher()
                            .extractPathWithinPattern("/subscribe/topic/*", headerAccessor.getDestination());
                    try {
                        entityManager
                                .createQuery("SELECT 1 FROM ChatSubscription s INNER JOIN s.chatRoom r WHERE s.account.id = ?1 AND r.id = ?2", Integer.class)
                                .setParameter(1, account.getId())
                                .setParameter(2, Long.parseLong(destination))
                                .setFirstResult(0)
                                .setMaxResults(1)
                                .getSingleResult();
                    } catch (NoResultException e) {
                        log.debug("계정 소유의 채팅방이 아닌 허가 되지 않은 채팅방에 접근할 수 없습니다.");
                        throw new IllegalArgumentException("계정 소유의 채팅방이 아닌 허가 되지 않은 채팅방에 접근할 수 없습니다.");
                    }
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
                .setApplicationDestinationPrefixes("/publish")
                .enableSimpleBroker("/subscribe");
    }

}
