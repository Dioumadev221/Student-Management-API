# Student Management API

API REST de gestion des étudiants de l'ISEP-AT, développée avec **Spring Boot 4** et **Java 25**.
Elle permet d'ajouter, modifier, supprimer, rechercher et lister des étudiants, avec une gestion
rigoureuse des codes HTTP, une **sécurité par JWT** et une documentation OpenAPI/Swagger.

## Sommaire

- [Stack technique](#stack-technique)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Configuration (profils)](#configuration-profils)
- [Lancer l'application](#lancer-lapplication)
- [Documentation Swagger](#documentation-swagger)
- [Endpoints](#endpoints)
- [Authentification (JWT)](#authentification-jwt)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Tests](#tests)
- [Décisions de conception](#décisions-de-conception)

## Stack technique

| Élément            | Version / Choix                          |
|--------------------|------------------------------------------|
| Langage            | Java 25                                  |
| Framework          | Spring Boot 4.1                          |
| Persistance        | Spring Data JPA / Hibernate              |
| Base de données    | PostgreSQL (H2 en mémoire pour les tests)|
| Sécurité           | Spring Security + JWT (JJWT), BCrypt      |
| Documentation      | springdoc-openapi 3 (Swagger UI)         |
| Boilerplate        | Lombok                                   |
| Build              | Maven (wrapper `mvnw`)                   |

## Architecture

Architecture en couches, chaque couche ayant une responsabilité unique :

```
controller/   → Exposition REST (Etudiant + Auth), traduction HTTP (couche mince)
service/      → Logique métier : validations manuelles, contrôles d'unicité, authentification
repository/   → Accès aux données (Spring Data JPA) : Etudiant, User
entity/       → Modèle de persistance : Etudiant, User, enum Role
dto/          → Contrats d'API (records) : Etudiant/Error + Register/Login/AuthResponse
mapper/       → Conversion entité ⇄ DTO
security/     → JWT : JwtUtils, JwtFilter, SecurityConfig
exception/    → Exceptions métier + @RestControllerAdvice global
config/       → Configuration OpenAPI (avec schéma de sécurité JWT)
```

Le flux d'une requête : `Controller → Service → Repository`, et en cas d'erreur, les exceptions
métier remontent jusqu'au `GlobalExceptionHandler` qui produit une réponse HTTP normalisée.

## Prérequis

- JDK 25
- PostgreSQL 14+ (pour les profils `dev` / `prod` ; les tests n'en ont pas besoin)

Créer les bases de données :

```sql
CREATE DATABASE isepat_dev;    -- profil dev
CREATE DATABASE isepat_cloud;  -- profil prod
```

## Configuration (profils)

| Profil | Base           | `ddl-auto` | Secrets                                   |
|--------|----------------|------------|-------------------------------------------|
| `dev`  | `isepat_dev`   | `update`   | valeurs par défaut (`postgres`/`postgres`)|
| `prod` | `isepat_cloud` | `validate` | **obligatoirement** via variables d'env   |
| `test` | H2 en mémoire  | `create-drop` | —                                      |

Le profil `dev` est actif par défaut. Les identifiants et la clé JWT sont surchargeables par
variables d'environnement (`DB_USERNAME`, `DB_PASSWORD`, `DB_HOST`, `DB_PORT`, `JWT_SECRET`,
`JWT_EXPIRATION`) — aucun secret n'est écrit en dur pour la production.

## Lancer l'application

```bash
# Profil dev (par défaut)
./mvnw spring-boot:run

# Profil prod
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

L'application démarre sur `http://localhost:8080`.

## Documentation Swagger

Une fois l'application lancée :

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **Spécification OpenAPI (JSON)** : http://localhost:8080/v3/api-docs

Chaque opération est documentée (objectif, paramètres, codes de retour et description).

## Endpoints

| Méthode | URL                            | Description                          | Succès |
|---------|--------------------------------|--------------------------------------|--------|
| POST    | `/etudiants`                   | Ajouter un étudiant                  | 201    |
| GET     | `/etudiants`                   | Lister les étudiants (triés par nom) | 200    |
| GET     | `/etudiants/{id}`              | Rechercher un étudiant par id        | 200    |
| GET     | `/etudiants/matricule/{matricule}` | Rechercher par matricule *(bonus)* | 200    |
| PUT     | `/etudiants/{id}`              | Modifier un étudiant                 | 200    |
| DELETE  | `/etudiants/{id}`              | Supprimer un étudiant                | 204    |

Exemple de corps de requête (POST/PUT) :

```json
{
  "matricule": "ET001",
  "prenom": "Moussa",
  "nom": "Diallo",
  "email": "moussa@universite.sn",
  "dateNaissance": "2003-04-15",
  "lieuNaissance": "Thiès",
  "nationalite": "Sénégalaise"
}
```

## Authentification (JWT)

L'API est protégée par **JSON Web Token**. Seuls `/api/auth/**` et la documentation Swagger sont
publics ; tous les endpoints `/etudiants/**` exigent un token valide.

| Méthode | URL                   | Description                          | Public |
|---------|-----------------------|--------------------------------------|--------|
| POST    | `/api/auth/register`  | Créer un compte (rôle USER) + token  | ✅     |
| POST    | `/api/auth/login`     | Se connecter et récupérer un token   | ✅     |

**Utilisation :**

1. S'inscrire ou se connecter → récupérer le `token` renvoyé.
2. Envoyer ce token dans l'en-tête de chaque requête protégée :
   ```
   Authorization: Bearer <token>
   ```
3. Dans **Swagger UI**, cliquer sur le bouton **Authorize** 🔒 et coller le token.

Le token contient le nom d'utilisateur et le rôle, est signé (HMAC-SHA) avec la clé `jwt.secret`
(externalisée via `JWT_SECRET` en production) et expire après `jwt.expiration` millisecondes.
Les mots de passe sont stockés hachés avec **BCrypt**.

## Gestion des erreurs

Les erreurs renvoient un corps normalisé `{ "code": <http>, "msg": "<message>" }` :

| Situation                     | Code HTTP        |
|-------------------------------|------------------|
| Création réussie              | 201 Created      |
| Modification réussie          | 200 OK           |
| Suppression réussie           | 204 No Content   |
| Champ obligatoire manquant    | 400 Bad Request  |
| Matricule déjà existant       | 409 Conflict     |
| Email déjà existant           | 409 Conflict     |
| Étudiant introuvable          | 404 Not Found    |
| Identifiants invalides        | 401 Unauthorized |

Exemple :

```json
{ "code": 400, "msg": "Le matricule est obligatoire." }
```

## Tests

```bash
./mvnw test
```

La suite couvre les quatre niveaux :

- **`EtudiantServiceTest`** — tests unitaires (Mockito) des règles de validation et d'unicité ;
- **`EtudiantRepositoryTest`** — `@DataJpaTest` sur H2 (requêtes dérivées, tri) ;
- **`EtudiantControllerTest`** — `@WebMvcTest` (mapping des codes HTTP et format d'erreur) ;
- **`EtudiantApiIntegrationTest`** — bout en bout sur H2 (scénarios du sujet + `/v3/api-docs`) ;
- **`AuthIntegrationTest`** — flux JWT complet (inscription → token → accès protégé, 401 sur mauvais identifiants).

## Décisions de conception

- **Validation manuelle** : conformément au sujet, les annotations `@Valid` / `@NotBlank` ne sont
  pas utilisées. Les contrôles sont réalisés à la main dans la couche **service** (où résident les
  règles métier) plutôt que dans le contrôleur, pour garder ce dernier mince et testable.
- **Gestion centralisée des erreurs** : un `@RestControllerAdvice` unique traduit les exceptions
  métier en codes HTTP, évitant de disperser cette logique dans chaque endpoint.
- **DTOs (records)** : l'entité JPA n'est jamais exposée directement. Les `record` Java servent de
  contrats d'API immuables et explicites.
- **Âge non persisté** : comme demandé, l'âge n'est ni stocké ni renvoyé par l'API ; il est calculé
  côté client à partir de `dateNaissance`.
- **Unicité en double garde** : garantie au niveau du schéma (contraintes SQL) *et* vérifiée en
  amont dans le service pour produire un message d'erreur explicite.
```
