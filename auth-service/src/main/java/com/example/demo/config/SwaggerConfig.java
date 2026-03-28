package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 🔥 THIS IS THE KEY FIX
                .addServersItem(new Server().url("/auth"))

                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0"));
    }
}