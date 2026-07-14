package com.diouma.studentmanagementapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Representation d'un etudiant renvoyee au client.
 */
@Schema(description = "Representation d'un etudiant renvoyee par l'API")
public record EtudiantResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "ET001")
        String matricule,

        @Schema(example = "Moussa")
        String prenom,

        @Schema(example = "Diallo")
        String nom,

        @Schema(example = "moussa@universite.sn")
        String email,

        @Schema(example = "2003-04-15")
        LocalDate dateNaissance,

        @Schema(example = "Thies")
        String lieuNaissance,

        @Schema(example = "Senegalaise")
        String nationalite
) {
}
