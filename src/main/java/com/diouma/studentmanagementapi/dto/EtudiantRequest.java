package com.diouma.studentmanagementapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Donnees d'entree recues du client pour creer ou modifier un etudiant.
 */
@Schema(description = "Donnees d'entree pour creer ou modifier un etudiant")
public record EtudiantRequest(

        @Schema(description = "Matricule unique de l'etudiant", example = "ET001")
        String matricule,

        @Schema(description = "Prenom", example = "Moussa")
        String prenom,

        @Schema(description = "Nom", example = "Diallo")
        String nom,

        @Schema(description = "Adresse e-mail unique", example = "moussa@universite.sn")
        String email,

        @Schema(description = "Date de naissance (format ISO aaaa-MM-jj)", example = "2003-04-15")
        LocalDate dateNaissance,

        @Schema(description = "Lieu de naissance", example = "Thies")
        String lieuNaissance,

        @Schema(description = "Nationalite", example = "Senegalaise")
        String nationalite
) {
}
