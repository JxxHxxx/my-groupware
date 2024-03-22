package com.jxx.vacation.api.common.configuration;

import com.jxx.vacation.api.common.interceptor.ApiAccessLogInterceptor;
import com.jxx.vacation.api.common.web.RestApiAuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebApiConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true); // Credentials Request 를 허용
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAccessLogInterceptor())
                .order(1);
        registry.addInterceptor(new RestApiAuthenticationInterceptor())
                .addPathPatterns("/api/vacations/**/vacation-status")
                .order(2);
    }
}
