package com.diouma.studentmanagementapi.controller;

import com.diouma.studentmanagementapi.dto.ErrorResponse;
import com.diouma.studentmanagementapi.dto.EtudiantRequest;
import com.diouma.studentmanagementapi.dto.EtudiantResponse;
import com.diouma.studentmanagementapi.service.EtudiantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/etudiants")
@RequiredArgsConstructor
@Tag(name = "Etudiants", description = "Points d'acces REST pour gerer les etudiants : creation, consultation, mise a jour et suppression")
public class EtudiantController {

    private final EtudiantService service;


    @Operation(
            summary = "Enregistrer un nouvel etudiant",
            description = "Ajoute un etudiant dans la base, apres avoir verifie que tous les champs "
                    + "obligatoires sont renseignes et que le matricule et l'email ne sont pas deja pris.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "L'etudiant a bien ete enregistre"),
            @ApiResponse(responseCode = "400", description = "Un des champs obligatoires n'a pas ete renseigne",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Le matricule ou l'email est deja utilise",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EtudiantResponse> ajouter(@RequestBody EtudiantRequest request,
                                                    UriComponentsBuilder uriBuilder) {
        EtudiantResponse cree = service.ajouter(request);
        URI localisation = uriBuilder.path("/etudiants/{id}")
                .buildAndExpand(cree.id())
                .toUri();
        return ResponseEntity.created(localisation).body(cree);
    }



    @Operation(
            summary = "Afficher tous les etudiants",
            description = "Retourne l'ensemble des etudiants enregistres, classes par nom dans l'ordre alphabetique.")
    @ApiResponse(responseCode = "200", description = "La liste des etudiants a ete renvoyee")
    @GetMapping
    public List<EtudiantResponse> lister() {
        return service.lister();
    }

    @Operation(
            summary = "Consulter un etudiant par son identifiant",
            description = "Retourne les informations d'un etudiant a partir de son identifiant technique (id).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "L'etudiant demande a ete trouve"),
            @ApiResponse(responseCode = "404", description = "Aucun etudiant ne correspond a cet identifiant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EtudiantResponse rechercher(@PathVariable Long id) {
        return service.rechercher(id);
    }





    @Operation(
            summary = "Trouver un etudiant a partir de son matricule",
            description = "Recherche un etudiant grace a son matricule unique (ex: ET001) "
                    + "plutot que par son identifiant technique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "L'etudiant correspondant a ete trouve"),
            @ApiResponse(responseCode = "404", description = "Aucun etudiant ne porte ce matricule",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/matricule/{matricule}")
    public EtudiantResponse rechercherParMatricule(@PathVariable String matricule) {
        return service.rechercherParMatricule(matricule);
    }



    @Operation(
            summary = "Mettre a jour un etudiant existant",
            description = "Modifie les informations d'un etudiant deja enregistre, apres verification des "
                    + "champs obligatoires et de l'unicite du matricule et de l'email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Les informations de l'etudiant ont ete mises a jour"),
            @ApiResponse(responseCode = "400", description = "Un des champs obligatoires n'a pas ete renseigne",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aucun etudiant ne correspond a cet identifiant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Le matricule ou l'email est deja utilise",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public EtudiantResponse modifier(@PathVariable Long id, @RequestBody EtudiantRequest request) {
        return service.modifier(id, request);
    }



    @Operation(
            summary = "Retirer un etudiant",
            description = "Supprime definitivement un etudiant de la base a partir de son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "L'etudiant a bien ete supprime"),
            @ApiResponse(responseCode = "404", description = "Aucun etudiant ne correspond a cet identifiant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
