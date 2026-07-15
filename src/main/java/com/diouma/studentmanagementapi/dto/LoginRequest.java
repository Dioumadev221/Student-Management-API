package com.diouma.studentmanagementapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Donnees de connexion")
public record LoginRequest(

        @Schema(description = "Nom d'utilisateur", example = "diouma")
        String username,

        @Schema(description = "Mot de passe", example = "MotDePasse123")
        String password
) {
}
