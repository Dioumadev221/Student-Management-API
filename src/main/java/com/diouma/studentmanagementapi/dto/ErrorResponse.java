package com.diouma.studentmanagementapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Corps de reponse standard en cas d'erreur.
 *
 * <p>Respecte le format impose par le cahier des charges :
 * {@code {"code": 400, "msg": "Le matricule est obligatoire."}}.</p>
 */
@Schema(description = "Corps de reponse renvoye en cas d'erreur")
public record ErrorResponse(

        @Schema(description = "Code HTTP de l'erreur", example = "400")
        int code,

        @Schema(description = "Message explicite decrivant l'erreur",
                example = "Le matricule est obligatoire.")
        String msg
) {
}
