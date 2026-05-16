package com.jciphertools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class JCipherToolsApplication {
    
    private static final Logger log = LoggerFactory.getLogger(JCipherToolsApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(JCipherToolsApplication.class, args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String activeProfile = System.getProperty("spring.profiles.active", "default");
        String port = System.getenv("SERVER_PORT") != null ? System.getenv("SERVER_PORT") : "8081";
        log.info("JCipherTools application started successfully - Active profile: {}, Server port: {}", activeProfile, port);
    }
}
