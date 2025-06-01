package com.example.redalert;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
    title = "AlertaÁgil API", 
    version = "v0.0.1",
    description = "API para o sistema de alertas colaborativos RedAlert."
))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER,
    description = "Autenticação JWT"
)
public class RedalertApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedalertApplication.class, args);
        System.out.println("Perfil Ativo: " + System.getProperty("spring.profiles.active", System.getenv("SPRING_PROFILES_ACTIVE") != null ? System.getenv("SPRING_PROFILES_ACTIVE") : "default (verifique application.properties)"));
        System.out.println("Swagger: http://localhost:8074/swagger-ui.html");
    }

}
