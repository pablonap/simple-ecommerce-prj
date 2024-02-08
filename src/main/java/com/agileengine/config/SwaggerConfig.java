package com.agileengine.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

        Components components = new Components()
            .addSecuritySchemes("bearerScheme", bearerScheme);

        return new OpenAPI()
            .components(components)
            .info(new Info()
                .title("Simple ecommerce prj")
                .version("1.0")
                .description("Simple ecommerce prj")
                .termsOfService("http://swagger.io/terms/"))
            .addSecurityItem(new SecurityRequirement().addList("bearerScheme"));
    }
}
