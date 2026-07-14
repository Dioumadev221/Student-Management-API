package com.diouma.studentmanagementapi.exception;

/**
 * Levee lorsqu'un champ obligatoire est absent ou que la requete est invalide.
 * Traduite en HTTP 400 (Bad Request) par le gestionnaire global.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
