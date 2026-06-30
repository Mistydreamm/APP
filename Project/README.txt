# Fichier README de l'application PhotoApp

Ce document explique le fonctionnement de l'application PhotoApp, comment la lancer et comment la modifier.

## 1. Fonctionnement de l'application

PhotoApp est une application web développée avec Spring Boot qui permet aux utilisateurs de télécharger et de parcourir des photos.

### Fonctionnalités principales :
- **Gestion des utilisateurs** : L'application gère trois types d'utilisateurs :
    1. **Anonyme** : Peut parcourir toutes les photos et les télécharger.
    2. **Utilisateur enregistré** : Peut faire tout ce qu'un utilisateur anonyme peut faire, et en plus :
        - S'inscrire avec un compte local en choisissant un forfait (FREE ou PRO).
        - Télécharger des photos avec une description et des hashtags.
        - Modifier ou supprimer ses propres photos.
    3. **Administrateur** : A tous les droits d'un utilisateur enregistré, et peut en plus modifier ou supprimer les photos de n'importe quel utilisateur.

- **Forfaits (Packages)** :
    - **FREE** : Limité à 5 téléchargements de photos par jour.
    - **PRO** : Limité à 50 téléchargements de photos par jour.

- **Stockage** :
    - Les métadonnées des photos (description, auteur, etc.) et les informations des utilisateurs sont stockées dans une base de données en mémoire (H2).
    - Les fichiers image sont sauvegardés sur le système de fichiers local, dans un dossier `uploads` créé à la racine du projet.

- **Journalisation (Logging)** : Les actions importantes (inscription, connexion, upload, download, etc.) sont enregistrées dans la base de données et affichées dans la console du serveur.

### Comment lancer l'application :
1.  Assurez-vous d'avoir Java (version 17 ou supérieure) et Maven installés.
2.  Ouvrez un terminal à la racine du projet.
3.  Lancez l'application avec la commande Maven : `mvn spring-boot:run`.
4.  Ouvrez votre navigateur et allez à l'adresse : `http://localhost:8081`.

### Utilisateur par défaut :
- Un compte administrateur est créé au démarrage :
    - **Nom d'utilisateur** : `admin`
    - **Mot de passe** : `admin123`

### Base de données :
- Pour accéder à la console de la base de données H2, allez sur `http://localhost:8081/h2-console`.
- **JDBC URL** : `jdbc:h2:mem:photodb`
- **Username** : `sa`
- **Password** : (laissez vide)

---

## 2. Guide des fichiers pour les modifications

Si vous souhaitez modifier l'application, voici un guide des fichiers et de leur rôle.

### Fichiers de configuration :
- **`pom.xml`** :
    - Gère les dépendances du projet (Spring Boot, base de données, sécurité, etc.).
    - C'est ici que vous ajoutez de nouvelles bibliothèques.

- **`src/main/resources/application.properties`** :
    - Fichier de configuration principal de Spring Boot.
    - Vous pouvez y changer le port du serveur (`server.port`), les paramètres de la base de données, le nom du dossier d'upload (`upload.dir`), etc.

### Code source Java (`src/main/java/hr/algebra/project/`) :
- **`model/`** : Contient les classes qui représentent les données (Entités JPA).
    - `AppUser.java` : Structure d'un utilisateur.
    - `Photo.java` : Structure des métadonnées d'une photo.
    - `ActionLog.java` : Structure d'une entrée de log.
    - `UserRole.java`, `PackageType.java` : Énumérations pour les rôles et les forfaits.

- **`repository/`** : Interfaces qui définissent les opérations sur la base de données (ex: trouver un utilisateur par son nom). Spring Data JPA s'occupe de l'implémentation.
    - `UserRepository.java`, `PhotoRepository.java`, `LogRepository.java`.

- **`service/`** : Contient la logique métier de l'application.
    - `UserService.java` : Gère l'inscription et la recherche d'utilisateurs.
    - `PhotoService.java` : Logique principale pour le téléchargement, la recherche, la suppression de photos et la vérification des limites de forfait.
    - `LoggingService.java` : Gère la création des logs.

- **`controller/`** : Gère les requêtes web (les URLs).
    - `PhotoController.java` : Gère les pages principales, l'upload, le téléchargement, l'édition et la suppression des photos.
    - `AuthController.java` : Gère les pages de connexion et d'inscription.

- **`security/`** : Configuration de la sécurité.
    - `SecurityConfig.java` : Définit les règles d'accès (quelles pages sont publiques, lesquelles nécessitent une connexion).
    - `CustomUserDetailsService.java` : Charge les informations de l'utilisateur pour que Spring Security puisse les utiliser.

- **`component/`** : Classes utilitaires.
    - `DataInitializer.java` : Crée l'utilisateur `admin` au démarrage de l'application.

### Fichiers de l'interface utilisateur (`src/main/resources/`) :
- **`templates/`** : Contient les fichiers HTML (avec la syntaxe Thymeleaf).
    - `base.html` : Le template de base (header, structure générale) utilisé par les autres pages.
    - `index.html` : La page d'accueil qui affiche la grille de photos.
    - `login.html`, `register.html` : Les pages de connexion et d'inscription.

- **`static/css/style.css`** : La feuille de style CSS. Modifiez ce fichier pour changer l'apparence visuelle de l'application.
