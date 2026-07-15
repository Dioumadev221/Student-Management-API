package com.diouma.studentmanagementapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration de la securite : prouve le flux JWT complet sur base H2, via la
 * vraie chaine de filtres de securite (sans @WithMockUser).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fluxComplet_inscription_puisAccesProtege_avecToken() throws Exception {
        // 1. Inscription -> 201 + token JWT
        String corpsInscription = """
                { "username": "diouma", "password": "MotDePasse123" }
                """;
        String reponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(corpsInscription))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.parse(reponse).read("$.token");

        // 2. Acces a un endpoint protege SANS token -> refuse (4xx)
        mockMvc.perform(get("/etudiants"))
                .andExpect(status().is4xxClientError());

        // 3. Acces AVEC le token -> autorise (200)
        mockMvc.perform(get("/etudiants")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void connexion_avecMauvaisMotDePasse_devraitRenvoyer401() throws Exception {
        // On cree d'abord le compte
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "username": "awa", "password": "BonMotDePasse" }
                        """))
                .andExpect(status().isCreated());

        // Puis on tente de se connecter avec un mauvais mot de passe -> 401
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "username": "awa", "password": "MauvaisMotDePasse" }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }
}
