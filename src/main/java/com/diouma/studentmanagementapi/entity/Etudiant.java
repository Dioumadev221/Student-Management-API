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
 *
 * <p>Remarque : l'age n'est volontairement pas persiste. Conformement au cahier
 * des charges, il est calcule cote client a partir de {@link #dateNaissance}.</p>
 *
 * <p>Les contraintes d'unicite ({@code matricule}, {@code email}) sont declarees
 * au niveau du schema (garantie forte cote base) <em>et</em> verifiees en amont
 * dans la couche service pour renvoyer un message d'erreur explicite.</p>
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

    /** Cle primaire technique, generee par la base de donnees. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant metier de l'etudiant : obligatoire et unique. */
    @Column(nullable = false, unique = true, length = 50)
    private String matricule;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    /** Adresse e-mail : obligatoire et unique. */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", nullable = false, length = 100)
    private String lieuNaissance;

    @Column(nullable = false, length = 100)
    private String nationalite;
}
