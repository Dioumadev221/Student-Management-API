package com.diouma.studentmanagementapi.repository;

import com.diouma.studentmanagementapi.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Couche d'acces aux donnees pour l'entite {@link Etudiant}.
 *
 * <p>Herite des operations CRUD standard de {@link JpaRepository} et ajoute les
 * requetes derivees necessaires aux controles metier et aux fonctionnalites bonus.</p>
 */
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    /** @return {@code true} si un etudiant possede deja ce matricule. */
    boolean existsByMatricule(String matricule);

    /** @return {@code true} si un etudiant possede deja cet email. */
    boolean existsByEmail(String email);

    /** Bonus : recherche d'un etudiant par son matricule. */
    Optional<Etudiant> findByMatricule(String matricule);

    /** Bonus : liste des etudiants triee par nom en ordre alphabetique. */
    List<Etudiant> findAllByOrderByNomAsc();
}
