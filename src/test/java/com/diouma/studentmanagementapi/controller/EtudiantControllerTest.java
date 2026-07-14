package com.diouma.studentmanagementapi.controller;

import com.diouma.studentmanagementapi.dto.EtudiantResponse;
import com.diouma.studentmanagementapi.exception.BadRequestException;
import com.diouma.studentmanagementapi.exception.DuplicateResourceException;
import com.diouma.studentmanagementapi.exception.ResourceNotFoundException;
import com.diouma.studentmanagementapi.service.EtudiantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la couche web : verifie le mapping des codes HTTP (201/204/400/404/409)
 * et le format du corps d'erreur, en mockant la couche service.
 */
@WebMvcTest(EtudiantController.class)
class EtudiantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EtudiantService service;

    private static final String CORPS_VALIDE = """
            {
              "matricule": "ET001",
              "prenom": "Moussa",
              "nom": "Diallo",
              "email": "moussa@universite.sn",
              "dateNaissance": "2003-04-15",
              "lieuNaissance": "Thies",
              "nationalite": "Senegalaise"
            }
            """;

    private EtudiantResponse reponseExemple() {
        return new EtudiantResponse(1L, "ET001", "Moussa", "Diallo",
                "moussa@universite.sn", LocalDate.of(2003, 4, 15), "Thies", "Senegalaise");
    }

    @Test
    void ajouter_devraitRenvoyer201() throws Exception {
        when(service.ajouter(any())).thenReturn(reponseExemple());

        mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPS_VALIDE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.matricule").value("ET001"));
    }

    @Test
    void ajouter_devraitRenvoyer409_quandMatriculeExistant() throws Exception {
        when(service.ajouter(any()))
                .thenThrow(new DuplicateResourceException("Le matricule 'ET001' existe deja."));

        mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPS_VALIDE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.msg").value("Le matricule 'ET001' existe deja."));
    }

    @Test
    void ajouter_devraitRenvoyer400_quandChampManquant() throws Exception {
        when(service.ajouter(any()))
                .thenThrow(new BadRequestException("Le prenom est obligatoire."));

        mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPS_VALIDE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("Le prenom est obligatoire."));
    }

    @Test
    void rechercher_devraitRenvoyer404_quandInexistant() throws Exception {
        when(service.rechercher(eq(100L)))
                .thenThrow(ResourceNotFoundException.parId(100L));

        mockMvc.perform(get("/etudiants/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void supprimer_devraitRenvoyer204() throws Exception {
        mockMvc.perform(delete("/etudiants/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void supprimer_devraitRenvoyer404_quandInexistant() throws Exception {
        doThrow(ResourceNotFoundException.parId(100L)).when(service).supprimer(100L);

        mockMvc.perform(delete("/etudiants/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
