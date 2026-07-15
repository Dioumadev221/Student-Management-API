package com.diouma.studentmanagementapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Reponse renvoyee apres une inscription ou une connexion :
 */
@Schema(description = "Token JWT et informations sur l'utilisateur authentifie")
public record AuthResponse(

        @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Type de token", example = "Bearer")
        String type,

        @Schema(example = "diouma")
        String username,

        @Schema(example = "USER")
        String role
) {
    public static AuthResponse bearer(String token, String username, String role) {
        return new AuthResponse(token, "Bearer", username, role);
    }
}
