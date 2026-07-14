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


@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository repository;
    private final EtudiantMapper mapper;

    /**
     * Ajoute un nouvel etudiant.
     * @throws BadRequestException si un champ obligatoire est manquant
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



    @Transactional
    public EtudiantResponse modifier(Long id, EtudiantRequest request) {
        Etudiant existant = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.parId(id));

        validerChamps(request);

        String matricule = request.matricule().trim();
        String email = request.email().trim();


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


    @Transactional(readOnly = true)
    public EtudiantResponse rechercher(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.parId(id));
    }


    @Transactional(readOnly = true)
    public EtudiantResponse rechercherParMatricule(String matricule) {
        return repository.findByMatricule(matricule)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun etudiant trouve pour le matricule '" + matricule + "'."));
    }

    @Transactional(readOnly = true)
    public List<EtudiantResponse> lister() {
        return repository.findAllByOrderByNomAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    @Transactional
    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException.parId(id);
        }
        repository.deleteById(id);
    }

    // Validation des champs obligatoires

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

    private void exigerTexte(String valeur, String messageErreur) {
        if (valeur == null || valeur.isBlank()) {
            throw new BadRequestException(messageErreur);
        }
    }
}
