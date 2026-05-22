package com.jciphertools.infrastructure.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jciphertools.infrastructure.config.properties.CorsProperties;

@Configuration
public class WebConfig implements WebMvcConfigurer  {
    
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    private final List<String> allowedOrigins;

    public WebConfig(CorsProperties corsProperties) {
        this.allowedOrigins = corsProperties.allowedOrigins();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS for /api/v1/** with allowed origins: {}", allowedOrigins);
        registry.addMapping("/api/v1/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept");
        log.debug("CORS configuration applied successfully");
    }
}
