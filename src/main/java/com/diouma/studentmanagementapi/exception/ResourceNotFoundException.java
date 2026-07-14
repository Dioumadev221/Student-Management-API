package com.diouma.studentmanagementapi.exception;

/**
 * Levee lorsqu'un etudiant recherche n'existe pas.
 * Traduite en HTTP 404 (Not Found) par le gestionnaire global.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException parId(Long id) {
        return new ResourceNotFoundException("Aucun etudiant trouve pour l'identifiant " + id + ".");
    }
}
