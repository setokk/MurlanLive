package org.murlan.um.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MLHeaderInterceptor implements HandlerInterceptor {
    @Value("${mulive.gameserver.secret.header}")
    private String gameServerSecretHeader;

    @Value("${mulive.gameserver.secret.header.val}")
    private String gameServerSecretHeaderVal;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String headerValue = request.getHeader(gameServerSecretHeader);

        if (headerValue == null || !headerValue.equals(gameServerSecretHeaderVal)) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing or invalid header");
            return false;
        }

        return true;
    }
}
