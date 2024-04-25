package com.jxx.vacation.api.common.configuration;

import com.jxx.vacation.api.common.interceptor.ApiAccessLogInterceptor;
import com.jxx.vacation.api.common.web.AdminApiAuthenticationInterceptor;
import com.jxx.vacation.api.member.application.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebApiConfiguration implements WebMvcConfigurer {

    private final AuthService authService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001", "https://main--jxx-gw.netlify.app")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true); // Credentials Request 를 허용
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAccessLogInterceptor())
                .order(1);
        registry.addInterceptor(new AdminApiAuthenticationInterceptor(authService))
                .addPathPatterns("/admin/**")
                .order(2);
    }
}
