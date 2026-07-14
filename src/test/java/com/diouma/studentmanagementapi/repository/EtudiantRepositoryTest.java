package com.diouma.studentmanagementapi.repository;

import com.diouma.studentmanagementapi.entity.Etudiant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'integration de la couche d'acces aux donnees sur base H2.
 * Verifie les requetes derivees (existence, recherche, tri).
 */
@DataJpaTest
@ActiveProfiles("test")
class EtudiantRepositoryTest {

    @Autowired
    private EtudiantRepository repository;

    @BeforeEach
    void seed() {
        repository.save(nouvelEtudiant("ET002", "Zenab", "Ndiaye", "zenab@universite.sn"));
        repository.save(nouvelEtudiant("ET001", "Moussa", "Diallo", "moussa@universite.sn"));
    }

    private Etudiant nouvelEtudiant(String matricule, String prenom, String nom, String email) {
        return Etudiant.builder()
                .matricule(matricule)
                .prenom(prenom)
                .nom(nom)
                .email(email)
                .dateNaissance(LocalDate.of(2003, 1, 1))
                .lieuNaissance("Dakar")
                .nationalite("Senegalaise")
                .build();
    }

    @Test
    void existsByMatricule_devraitRefleterLaPresence() {
        assertThat(repository.existsByMatricule("ET001")).isTrue();
        assertThat(repository.existsByMatricule("INCONNU")).isFalse();
    }

    @Test
    void existsByEmail_devraitRefleterLaPresence() {
        assertThat(repository.existsByEmail("moussa@universite.sn")).isTrue();
        assertThat(repository.existsByEmail("inconnu@universite.sn")).isFalse();
    }

    @Test
    void findByMatricule_devraitRetournerLEtudiant() {
        assertThat(repository.findByMatricule("ET001"))
                .isPresent()
                .get()
                .extracting(Etudiant::getNom)
                .isEqualTo("Diallo");
    }

    @Test
    void findAllByOrderByNomAsc_devraitTrierParNom() {
        List<Etudiant> etudiants = repository.findAllByOrderByNomAsc();

        assertThat(etudiants)
                .extracting(Etudiant::getNom)
                .containsExactly("Diallo", "Ndiaye");
    }
}
