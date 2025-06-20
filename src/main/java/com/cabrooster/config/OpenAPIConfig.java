package com.cabrooster.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Bean
    public OpenAPI cabRoosterOpenAPI() {
        Server server = new Server();
        server.setUrl(contextPath);
        server.setDescription("Server URL");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info().title("Cab Rooster Management API")
                        .description("API documentation for Cab Rooster Management System")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@cabrooster.com")));
    }
}
