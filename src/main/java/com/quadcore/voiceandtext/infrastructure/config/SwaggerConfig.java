package com.quadcore.voiceandtext.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(apiInfo())
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

    private Components components() {
        Components components = new Components();
        
        // Bearer Token 스키마 정의
        components.addSecuritySchemes("bearer-jwt",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Access Token을 사용한 인증\n\nExample: Bearer {accessToken}"));
        
        return components;
    }
}
