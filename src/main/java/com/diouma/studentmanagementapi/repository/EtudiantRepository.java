package com.diouma.studentmanagementapi.repository;

import com.diouma.studentmanagementapi.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    boolean existsByMatricule(String matricule);

    boolean existsByEmail(String email);

    Optional<Etudiant> findByMatricule(String matricule);

    List<Etudiant> findAllByOrderByNomAsc();
}
