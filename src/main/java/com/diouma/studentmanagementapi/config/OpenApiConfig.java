package com.diouma.studentmanagementapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_JWT = "bearerAuth";

    @Bean
    public OpenAPI studentManagementOpenAPI() {
        return new OpenAPI()
                // Declare le schema de securite JWT et l'applique globalement :
                // Swagger UI affiche alors un bouton "Authorize" pour saisir le token.
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_JWT))
                .components(new Components().addSecuritySchemes(SCHEME_JWT,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
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
