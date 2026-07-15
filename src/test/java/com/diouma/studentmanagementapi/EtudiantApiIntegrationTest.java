package com.diouma.studentmanagementapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration de bout en bout : contexte Spring complet (base H2) exerce a
 * travers la vraie pile MVC. Couvre les scenarios du cahier des charges (section X)
 * ainsi que la disponibilite de la documentation OpenAPI generee par springdoc.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // focalise sur le CRUD ; la securite est testee par AuthIntegrationTest
@ActiveProfiles("test")
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
class EtudiantApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ET001 = """
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

    @Test
    void scenarioComplet_crud_etCodesHttp() throws Exception {
        // Test 1 : ajout -> 201 Created
        String reponse = mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON).content(ET001))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricule").value("ET001"))
                .andReturn().getResponse().getContentAsString();
        long id = com.jayway.jsonpath.JsonPath.parse(reponse).read("$.id", Long.class);

        // Test 2 : matricule deja existant -> 409 Conflict
        String memeMatricule = ET001.replace("moussa@universite.sn", "autre@universite.sn");
        mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON).content(memeMatricule))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));

        // Test 3 : email deja existant -> 409 Conflict
        String memeEmail = ET001.replace("ET001", "ET999");
        mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON).content(memeEmail))
                .andExpect(status().isConflict());

        // Test 4 : champ obligatoire manquant (prenom vide) -> 400 Bad Request
        String prenomVide = ET001.replace("\"Moussa\"", "\"\"").replace("ET001", "ET002")
                .replace("moussa@universite.sn", "vide@universite.sn");
        mockMvc.perform(post("/etudiants")
                        .contentType(MediaType.APPLICATION_JSON).content(prenomVide))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("Le prenom est obligatoire."));

        // Test 5 : recherche d'un etudiant inexistant -> 404 Not Found
        mockMvc.perform(get("/etudiants/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        // Recherche de l'etudiant cree -> 200 OK
        mockMvc.perform(get("/etudiants/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule").value("ET001"));

        // Test 6 : suppression -> 204 No Content
        mockMvc.perform(delete("/etudiants/" + id))
                .andExpect(status().isNoContent());

        // Apres suppression -> 404
        mockMvc.perform(get("/etudiants/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void documentationOpenApi_devraitEtreDisponible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths./etudiants").exists());
    }
}
