package com.fresh.product.config;

import com.fresh.product.interceptor.UserInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        "/category/tree",
                        "/category/list",
                        "/product/list",
                        "/product/detail/**",
                        "/product/sku/list",
                        "/stock/**"
                );
    }
}
