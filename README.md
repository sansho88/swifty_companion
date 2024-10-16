# Swifty Companion

## Description

Swifty Companion est une app Android élégamment conçue en Kotlin. Elle récupère et affiche, via l'API 42, toutes les infos clés des étudiants de l'école 42. Avec une interface fluide et intuitive, elle permet de rechercher rapidement un utilisateur et de consulter ses projets, compétences, et détails personnels en quelques clics.

## Fonctionnalités

- **Recherche d'utilisateur** : trouver un etudiant via son login
- **Affichage des informations utilisateur** :
  - Login
  - Email et mobile
  - Niveau et localisation à 42
  - Projets effectués (réussis et échoués)
  - Compétences et pourcentage
- **Navigation fluide** entre les vues principales.
- **Gestion des erreurs** : connexion échouée, utilisateur non trouvé, problèmes réseau, etc.

## Prérequis

- Un compte 42 avec un **API UID et un Secret** (pour l'authentification OAuth2).
- Environnement de développement Android (Android Studio).
- Version 2 de l'API 42 (la plus recente a ce moment).

## Installation

1. Clonez le dépôt Git :

   ```bash
   git clone https://github.com/sansho88/swifty_companion.git
   ```

2. Ouvrez le projet dans **Android Studio**.

3. Créez un fichier `local.properties` pour stocker vos informations confidentielles :

   ```properties
    API_UID=your_uid API_SECRET=your_secret
   ```

4. Synchronisez les dépendances et lancez l'application sur un émulateur ou un appareil physique :

   ```bash
   ./gradlew build
   ```

## Utilisation

1. Ouvrez l'application.
2. Recherchez un étudiant en entrant son login.
3. Consultez les informations détaillées sur l'utilisateur, y compris ses projets et compétences.

## Dépendances

- **Retrofit** : pour les appels API réseau.
- **Gson** : pour la sérialisation/désérialisation JSON.
- **Picasso** : pour le chargement des images des profils.

## Ressources

- Documentation API 42 : <https://api.intra.42.fr/apidoc>
- Tutoriels Kotlin pour Android :
  - [Android Developers](https://developer.android.com/kotlin)
  - [YouTube : Coding in Flow](https://www.youtube.com/channel/UC_Fh8kvtkVPkeihBs42jGcA)

## Auteur

Projet réalisé par Thomas GRIFFITH dans le cadre du cursus 42.