package com.diouma.studentmanagementapi.mapper;

import com.diouma.studentmanagementapi.dto.EtudiantRequest;
import com.diouma.studentmanagementapi.dto.EtudiantResponse;
import com.diouma.studentmanagementapi.entity.Etudiant;
import org.springframework.stereotype.Component;

/**
 * Convertit entre l'entite {@link Etudiant} et ses DTOs.
 *
 * <p>Centraliser le mapping ici evite de dupliquer la logique de conversion dans
 * le service et garantit que l'entite ne fuit jamais hors de la couche metier.</p>
 */
@Component
public class EtudiantMapper {

    /** Construit une nouvelle entite a partir des donnees d'entree (creation). */
    public Etudiant toEntity(EtudiantRequest request) {
        return Etudiant.builder()
                .matricule(normaliser(request.matricule()))
                .prenom(normaliser(request.prenom()))
                .nom(normaliser(request.nom()))
                .email(normaliser(request.email()))
                .dateNaissance(request.dateNaissance())
                .lieuNaissance(normaliser(request.lieuNaissance()))
                .nationalite(normaliser(request.nationalite()))
                .build();
    }

    /** Reporte les champs modifiables du DTO sur une entite existante (mise a jour). */
    public void updateEntity(Etudiant cible, EtudiantRequest request) {
        cible.setMatricule(normaliser(request.matricule()));
        cible.setPrenom(normaliser(request.prenom()));
        cible.setNom(normaliser(request.nom()));
        cible.setEmail(normaliser(request.email()));
        cible.setDateNaissance(request.dateNaissance());
        cible.setLieuNaissance(normaliser(request.lieuNaissance()));
        cible.setNationalite(normaliser(request.nationalite()));
    }

    /** Transforme une entite en representation exposee au client. */
    public EtudiantResponse toResponse(Etudiant etudiant) {
        return new EtudiantResponse(
                etudiant.getId(),
                etudiant.getMatricule(),
                etudiant.getPrenom(),
                etudiant.getNom(),
                etudiant.getEmail(),
                etudiant.getDateNaissance(),
                etudiant.getLieuNaissance(),
                etudiant.getNationalite()
        );
    }

    /** Supprime les espaces superflus ; conserve {@code null} tel quel. */
    private String normaliser(String valeur) {
        return valeur == null ? null : valeur.trim();
    }
}
