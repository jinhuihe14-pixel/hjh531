package com.fresh.delivery.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.common.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Value("${auth.token.secret:fresh-auth-secret-key-2024-abcdefghijklmnopqrstuvwxyz}")
    private String tokenSecret;

    @Value("${auth.token.expire:7200}")
    private Long tokenExpire;

    private JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        this.jwtUtil = new JwtUtil(tokenSecret, tokenExpire * 1000);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }

        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Long userId = jwtUtil.getUserId(token);
            if (userId == null) {
                throw new BusinessException(ResultCode.TOKEN_INVALID);
            }
            UserContext.setUserId(userId);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
