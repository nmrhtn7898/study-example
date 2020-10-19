package com.nuguri.example.config;

import com.nuguri.example.annotation.WebSocketPrincipal;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class WebSocketPrincipalResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WebSocketPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        return ((UsernamePasswordAuthenticationToken) headerAccessor.getUser()).getPrincipal();
    }

}
