package com.fresh.product.interceptor;

import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil = new JwtUtil();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        if (jwtUtil.isExpired(token)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        Long userId = jwtUtil.getUserId(token);
        UserContext.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
