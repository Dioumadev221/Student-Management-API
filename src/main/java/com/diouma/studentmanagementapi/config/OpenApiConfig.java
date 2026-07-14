package com.diouma.studentmanagementapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Management API")
                        .description("API REST de gestion des etudiants de l'ISEP-AT. "
                                + "Permet d'ajouter, modifier, supprimer, rechercher et lister les etudiants.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Diouma DIONE")
                                .email("dioumadione004@gmail.com"))
                        .license(new License().name("Usage pedagogique")));
    }
}
