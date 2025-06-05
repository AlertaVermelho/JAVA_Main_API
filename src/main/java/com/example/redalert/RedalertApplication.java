package com.example.redalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "RedAlert API", 
        version = "v0.0.1",
        description = "API para o sistema de alertas colaborativos RedAlert." 
    ),
    servers = {
        @Server(url = "/", description = "URL Base Relativa do Servidor")
    }
)
@SecurityScheme(
    name = "bearerAuth", 
    type = SecuritySchemeType.HTTP, 
    bearerFormat = "JWT",          
    scheme = "bearer",             
    in = SecuritySchemeIn.HEADER,   
    description = "Autenticação JWT. Insira o token Bearer no formato: Bearer <token>"
)
public class RedalertApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedalertApplication.class, args);
        System.out.println(">>> RedAlert API Iniciada <<<");
        
        String activeProfile = System.getProperty("spring.profiles.active");
        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        if (activeProfile == null || activeProfile.isEmpty()) {
            System.out.println(">>> Perfil(s) Ativo(s): " + System.getProperty("spring.profiles.active", System.getenv("SPRING_PROFILES_ACTIVE") != null ? System.getenv("SPRING_PROFILES_ACTIVE") : "default (verifique application.properties)"));
        } else {
            System.out.println(">>> Perfil(s) Ativo(s) via System.getProperty ou SPRING_PROFILES_ACTIVE: " + activeProfile);
        }
        
        System.out.println(">>> Documentação Swagger UI disponível em: /swagger-ui/index.html (relativo à URL base da aplicação)");
    }
}
