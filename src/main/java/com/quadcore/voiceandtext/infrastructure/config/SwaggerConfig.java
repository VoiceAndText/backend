package com.quadcore.voiceandtext.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .components(components());
    }

    private Info apiInfo() {
        return new Info()
                .title("Voice and Text API")
                .description("Voice and Text 애플리케이션 REST API 문서")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Quadcore")
                        .url("https://quadcore.com")
                        .email("support@quadcore.com"));
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("https://voiceandtext.duckdns.org")
                        .description("Production Server")
        );
    }

    private Components components() {
        Components components = new Components();

        components.addSecuritySchemes("bearer-jwt",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Access Token을 사용한 인증\n\nExample: Bearer {accessToken}"));

        return components;
    }
}