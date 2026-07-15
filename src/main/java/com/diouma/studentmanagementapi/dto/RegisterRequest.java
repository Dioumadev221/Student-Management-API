package com.diouma.studentmanagementapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Donnees d'inscription */
@Schema(description = "Donnees d'inscription")
public record RegisterRequest(

        @Schema(description = "Nom d'utilisateur unique", example = "diouma")
        String username,

        @Schema(description = "Mot de passe", example = "MotDePasse123")
        String password
) {
}
