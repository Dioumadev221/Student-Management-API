package com.diouma.studentmanagementapi.exception;

/**
 * Levee lorsqu'une contrainte d'unicite est violee (matricule ou email deja utilise).
 * Traduite en HTTP 409 (Conflict) par le gestionnaire global.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
