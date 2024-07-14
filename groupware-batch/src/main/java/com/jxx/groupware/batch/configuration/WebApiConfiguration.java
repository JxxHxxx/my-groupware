package com.jxx.groupware.batch.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebApiConfiguration implements WebMvcConfigurer {
    private static final String ADMIN_DOMAIN = "http://localhost:3100";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ADMIN_DOMAIN)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
