package com.jciphertools.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jcipherToolsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("JCipherTools API")
                .version("v1")
                .description("REST API to encrypt and decrypt data using RSA-OAEP, AES-CBC-256, and AES-GCM-256.")
                .contact(new Contact()
                    .name("JCipherTools")
                    .url("https://github.com/victor-molinero/jciphertools"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(new Server()
                .url("http://localhost:8081")
                .description("Local development server")));
    }
}