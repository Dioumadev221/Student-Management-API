package com.diouma.studentmanagementapi.service;

import com.diouma.studentmanagementapi.dto.EtudiantRequest;
import com.diouma.studentmanagementapi.dto.EtudiantResponse;
import com.diouma.studentmanagementapi.entity.Etudiant;
import com.diouma.studentmanagementapi.exception.BadRequestException;
import com.diouma.studentmanagementapi.exception.DuplicateResourceException;
import com.diouma.studentmanagementapi.exception.ResourceNotFoundException;
import com.diouma.studentmanagementapi.mapper.EtudiantMapper;
import com.diouma.studentmanagementapi.repository.EtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche metier de gestion des etudiants.
 *
 * <p>Elle porte l'ensemble des regles de gestion :</p>
 * <ul>
 *     <li>validation manuelle des champs obligatoires (les annotations
 *         {@code @Valid}/{@code @NotBlank} ne sont pas utilisees, conformement au sujet) ;</li>
 *     <li>controle d'unicite du matricule et de l'email ;</li>
 *     <li>traduction des cas d'erreur en exceptions expressives, converties en
 *         codes HTTP par le gestionnaire global.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository repository;
    private final EtudiantMapper mapper;

    /**
     * Ajoute un nouvel etudiant.
     *
     * @throws BadRequestException       si un champ obligatoire est manquant
     * @throws DuplicateResourceException si le matricule ou l'email existe deja
     */
    @Transactional
    public EtudiantResponse ajouter(EtudiantRequest request) {
        validerChamps(request);

        String matricule = request.matricule().trim();
        String email = request.email().trim();

        if (repository.existsByMatricule(matricule)) {
            throw new DuplicateResourceException(
                    "Le matricule '" + matricule + "' existe deja.");
        }
        if (repository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "L'email '" + email + "' existe deja.");
        }

        Etudiant enregistre = repository.save(mapper.toEntity(request));
        return mapper.toResponse(enregistre);
    }

    /**
     * Modifie un etudiant existant.
     *
     * @throws ResourceNotFoundException  si aucun etudiant ne correspond a l'id
     * @throws BadRequestException        si un champ obligatoire est manquant
     * @throws DuplicateResourceException si le nouveau matricule/email appartient a un autre etudiant
     */
    @Transactional
    public EtudiantResponse modifier(Long id, EtudiantRequest request) {
        Etudiant existant = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.parId(id));

        validerChamps(request);

        String matricule = request.matricule().trim();
        String email = request.email().trim();

        // On ne signale un conflit que si la valeur a change ET qu'elle est deja prise par un autre.
        if (!matricule.equals(existant.getMatricule()) && repository.existsByMatricule(matricule)) {
            throw new DuplicateResourceException(
                    "Le matricule '" + matricule + "' existe deja.");
        }
        if (!email.equals(existant.getEmail()) && repository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "L'email '" + email + "' existe deja.");
        }

        mapper.updateEntity(existant, request);
        return mapper.toResponse(repository.save(existant));
    }

    /**
     * Recherche un etudiant par son identifiant technique.
     *
     * @throws ResourceNotFoundException si aucun etudiant ne correspond a l'id
     */
    @Transactional(readOnly = true)
    public EtudiantResponse rechercher(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.parId(id));
    }

    /**
     * Bonus : recherche un etudiant par son matricule.
     *
     * @throws ResourceNotFoundException si aucun etudiant ne correspond au matricule
     */
    @Transactional(readOnly = true)
    public EtudiantResponse rechercherParMatricule(String matricule) {
        return repository.findByMatricule(matricule)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun etudiant trouve pour le matricule '" + matricule + "'."));
    }

    /** Liste tous les etudiants, tries par nom en ordre alphabetique (bonus). */
    @Transactional(readOnly = true)
    public List<EtudiantResponse> lister() {
        return repository.findAllByOrderByNomAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Supprime un etudiant.
     *
     * @throws ResourceNotFoundException si aucun etudiant ne correspond a l'id
     */
    @Transactional
    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException.parId(id);
        }
        repository.deleteById(id);
    }

    // ------------------------------------------------------------------
    // Validation manuelle des champs obligatoires (aucune annotation @Valid)
    // ------------------------------------------------------------------

    private void validerChamps(EtudiantRequest r) {
        if (r == null) {
            throw new BadRequestException("Le corps de la requete est obligatoire.");
        }
        exigerTexte(r.matricule(), "Le matricule est obligatoire.");
        exigerTexte(r.prenom(), "Le prenom est obligatoire.");
        exigerTexte(r.nom(), "Le nom est obligatoire.");
        exigerTexte(r.email(), "L'email est obligatoire.");
        if (r.dateNaissance() == null) {
            throw new BadRequestException("La date de naissance est obligatoire.");
        }
        exigerTexte(r.lieuNaissance(), "Le lieu de naissance est obligatoire.");
        exigerTexte(r.nationalite(), "La nationalite est obligatoire.");
    }

    /** Verifie qu'une chaine est renseignee (non nulle et non composee uniquement d'espaces). */
    private void exigerTexte(String valeur, String messageErreur) {
        if (valeur == null || valeur.isBlank()) {
            throw new BadRequestException(messageErreur);
        }
    }
}
