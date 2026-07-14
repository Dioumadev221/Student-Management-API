package com.diouma.studentmanagementapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entite JPA representant un etudiant de l'ISEP-AT.
 * Remarque : l'age n'est volontairement pas persiste il est calcule cote client a partir de dateNaissance
 */
@Entity
@Table(
        name = "etudiants",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_etudiant_matricule", columnNames = "matricule"),
                @UniqueConstraint(name = "uk_etudiant_email", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String matricule;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", nullable = false, length = 100)
    private String lieuNaissance;

    @Column(nullable = false, length = 100)
    private String nationalite;
}
