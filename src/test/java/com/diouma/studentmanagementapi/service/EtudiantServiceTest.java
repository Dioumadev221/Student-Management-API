package com.diouma.studentmanagementapi.service;

import com.diouma.studentmanagementapi.dto.EtudiantRequest;
import com.diouma.studentmanagementapi.dto.EtudiantResponse;
import com.diouma.studentmanagementapi.entity.Etudiant;
import com.diouma.studentmanagementapi.exception.BadRequestException;
import com.diouma.studentmanagementapi.exception.DuplicateResourceException;
import com.diouma.studentmanagementapi.exception.ResourceNotFoundException;
import com.diouma.studentmanagementapi.mapper.EtudiantMapper;
import com.diouma.studentmanagementapi.repository.EtudiantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de la couche metier : les regles de validation et de controle
 * d'unicite sont verifiees en isolant le service de la base grace a Mockito.
 */
@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository repository;

    private final EtudiantMapper mapper = new EtudiantMapper();

    private EtudiantService service;

    @BeforeEach
    void setUp() {
        service = new EtudiantService(repository, mapper);
    }

    private EtudiantRequest requeteValide() {
        return new EtudiantRequest("ET001", "Moussa", "Diallo",
                "moussa@universite.sn", LocalDate.of(2003, 4, 15), "Thies", "Senegalaise");
    }

    @Test
    void ajouter_devraitEnregistrer_quandDonneesValides() {
        when(repository.existsByMatricule("ET001")).thenReturn(false);
        when(repository.existsByEmail("moussa@universite.sn")).thenReturn(false);
        when(repository.save(any(Etudiant.class))).thenAnswer(invocation -> {
            Etudiant e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        EtudiantResponse response = service.ajouter(requeteValide());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.matricule()).isEqualTo("ET001");
        verify(repository).save(any(Etudiant.class));
    }

    @Test
    void ajouter_devraitRejeter_quandPrenomVide() {
        EtudiantRequest requete = new EtudiantRequest("ET001", "   ", "Diallo",
                "moussa@universite.sn", LocalDate.of(2003, 4, 15), "Thies", "Senegalaise");

        assertThatThrownBy(() -> service.ajouter(requete))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("prenom");
        verify(repository, never()).save(any());
    }

    @Test
    void ajouter_devraitRejeter_quandDateNaissanceNulle() {
        EtudiantRequest requete = new EtudiantRequest("ET001", "Moussa", "Diallo",
                "moussa@universite.sn", null, "Thies", "Senegalaise");

        assertThatThrownBy(() -> service.ajouter(requete))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("date de naissance");
    }

    @Test
    void ajouter_devraitRejeter_quandMatriculeDejaExistant() {
        when(repository.existsByMatricule("ET001")).thenReturn(true);

        assertThatThrownBy(() -> service.ajouter(requeteValide()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("matricule");
        verify(repository, never()).save(any());
    }

    @Test
    void ajouter_devraitRejeter_quandEmailDejaExistant() {
        when(repository.existsByMatricule("ET001")).thenReturn(false);
        when(repository.existsByEmail("moussa@universite.sn")).thenReturn(true);

        assertThatThrownBy(() -> service.ajouter(requeteValide()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
    }

    @Test
    void rechercher_devraitLeverNotFound_quandIdInexistant() {
        when(repository.findById(100L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.rechercher(100L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void supprimer_devraitLeverNotFound_quandIdInexistant() {
        when(repository.existsById(100L)).thenReturn(false);

        assertThatThrownBy(() -> service.supprimer(100L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
