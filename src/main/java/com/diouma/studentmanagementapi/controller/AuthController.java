package com.diouma.studentmanagementapi.controller;

import com.diouma.studentmanagementapi.dto.AuthResponse;
import com.diouma.studentmanagementapi.dto.ErrorResponse;
import com.diouma.studentmanagementapi.dto.LoginRequest;
import com.diouma.studentmanagementapi.dto.RegisterRequest;
import com.diouma.studentmanagementapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Inscrire un nouvel utilisateur",
            description = "Cree un compte utilisateur (role USER) et renvoie un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte cree, token renvoye"),
            @ApiResponse(responseCode = "400", description = "Nom d'utilisateur ou mot de passe manquant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nom d'utilisateur deja pris",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(
            summary = "Se connecter",
            description = "Verifie les identifiants et renvoie un token JWT a utiliser dans "
                    + "l'en-tete Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion reussie, token renvoye"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
