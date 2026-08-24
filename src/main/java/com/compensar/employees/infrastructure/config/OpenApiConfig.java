package com.compensar.employees.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employees API")
                        .description("API REST para la gestión de empleados y sus jornadas laborales")
                        .version("v1.0.0"));
    }
}
