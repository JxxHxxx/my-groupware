package com.jxx.groupware.api.common.configuration;

import com.jxx.groupware.api.common.interceptor.ApiAccessLogInterceptor;
import com.jxx.groupware.api.common.web.AdminApiAuthenticationInterceptor;
import com.jxx.groupware.api.member.application.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebApiConfiguration implements WebMvcConfigurer {

    private final AuthService authService;
    private static final String ADMIN_DOMAIN = "http://localhost:3100";
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001", "https://main--jxx-gw.netlify.app", ADMIN_DOMAIN)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true); // Credentials Request 를 허용
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminApiAuthenticationInterceptor(authService))
                .addPathPatterns("/admin/**")
                .order(1);

        registry.addInterceptor(new ApiAccessLogInterceptor())
                .order(2);
    }
}
