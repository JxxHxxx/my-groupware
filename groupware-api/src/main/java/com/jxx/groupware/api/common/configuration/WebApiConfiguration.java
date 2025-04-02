package com.jxx.groupware.api.common.configuration;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jxx.groupware.api.common.interceptor.ApiAccessLogInterceptor;
import com.jxx.groupware.api.common.web.AdminApiAuthenticationInterceptor;
import com.jxx.groupware.api.member.application.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

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


    // LocalDate, LocalDateTime serialize format Global Setting
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DATE_TIME_FORMAT);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        };
    }
}
