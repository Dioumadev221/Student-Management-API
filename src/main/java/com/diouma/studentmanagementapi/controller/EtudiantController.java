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
@Tag(name = "Etudiants", description = "Gestion des etudiants de l'ISEP-AT operations CRUD")
public class EtudiantController {

    private final EtudiantService service;


    @Operation(
            summary = "Ajouter un etudiant",
            description = "Cree un nouvel etudiant apres verification des champs obligatoires "
                    + "et de l'unicite du matricule et de l'email.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Etudiant cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Matricule ou email deja existant",
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
            summary = "Lister les etudiants",
            description = "Renvoie la liste de tous les etudiants, triee par nom en ordre alphabetique.")
    @ApiResponse(responseCode = "200", description = "Liste renvoyee avec succes")
    @GetMapping
    public List<EtudiantResponse> lister() {
        return service.lister();
    }

    @Operation(
            summary = "Rechercher un etudiant par identifiant",
            description = "Renvoie l'etudiant correspondant a l'identifiant technique fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etudiant trouve"),
            @ApiResponse(responseCode = "404", description = "Etudiant introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EtudiantResponse rechercher(@PathVariable Long id) {
        return service.rechercher(id);
    }





    @Operation(
            summary = "Rechercher un etudiant par matricule",
            description = "Renvoie l'etudiant correspondant au matricule fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etudiant trouve"),
            @ApiResponse(responseCode = "404", description = "Etudiant introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/matricule/{matricule}")
    public EtudiantResponse rechercherParMatricule(@PathVariable String matricule) {
        return service.rechercherParMatricule(matricule);
    }



    @Operation(
            summary = "Modifier un etudiant",
            description = "Met a jour un etudiant existant apres verification des champs "
                    + "obligatoires et de l'unicite du matricule et de l'email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etudiant modifie avec succes"),
            @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Etudiant introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Matricule ou email deja existant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public EtudiantResponse modifier(@PathVariable Long id, @RequestBody EtudiantRequest request) {
        return service.modifier(id, request);
    }



    @Operation(
            summary = "Supprimer un etudiant",
            description = "Supprime l'etudiant correspondant a l'identifiant fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Etudiant supprime avec succes"),
            @ApiResponse(responseCode = "404", description = "Etudiant introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
