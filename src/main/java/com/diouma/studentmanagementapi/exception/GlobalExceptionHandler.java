package com.diouma.studentmanagementapi.exception;

import com.diouma.studentmanagementapi.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Gestionnaire d'exceptions centralise pour l'ensemble de l'API.
 *
 * <p>Choix d'architecture : plutot que de melanger la logique de traduction des
 * erreurs en codes HTTP dans chaque methode du controleur, on la centralise ici.
 * Les couches metier levent des exceptions expressives ; ce conseiller les
 * convertit en reponses HTTP normalisees au format {@link ErrorResponse}.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Champ obligatoire manquant ou requete invalide -> 400 Bad Request. */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Etudiant introuvable -> 404 Not Found. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Violation d'unicite (matricule / email deja existant) -> 409 Conflict. */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DuplicateResourceException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Corps JSON illisible ou date mal formee -> 400 Bad Request. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "Le corps de la requete est illisible ou mal forme (verifiez le format des champs, ex: date aaaa-MM-jj).");
    }

    /** Type de parametre d'URL invalide (ex: /etudiants/abc) -> 400 Bad Request. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "Parametre '" + ex.getName() + "' invalide : une valeur numerique est attendue.");
    }

    /** Filet de securite : toute erreur non prevue -> 500 Internal Server Error. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message));
    }
}
