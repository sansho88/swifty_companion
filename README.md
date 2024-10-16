# Swifty Companion

## Description

**Swifty Companion** est une app Android realisée en **Kotlin**. Elle récupère et affiche, via des requetes vers l'API 42, toutes les infos clés des étudiants du réseau 42.
Il est possible de rechercher rapidement un utilisateur et de consulter ses projets, compétences, et détails personnels facilement.

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

3. Créez un fichier `env` (sans '.') dans le chemin: 'swifty_companion/app/src/main/assets/'
   Il doit contenir vos informations confidentielles :

```properties
UID=
SECRET=
```

Les infos necessaires peuvent etre trouvees, sur l'intranet 42, dans les parametres du profil

4. Synchronisez les dépendances et lancez l'application sur un émulateur ou un appareil physique :

   ```bash
   ./gradlew build
   ```

## Utilisation

1. Ouvrez l'application.
2. Recherchez un étudiant en entrant son login.
3. Consultez les informations détaillées sur l'utilisateur, y compris ses projets et compétences.

## Dépendances externes

- **AndroidX Core & AppCompat**

 2. **Material Design**

 3. **Constraint Layout**

 4. **Annotation**

 5. **Lifecycle Components**

 6. **Google Play Services**

 7. **Gson (JSON Parsing)**

 8. **Fuel (HTTP Networking)**

 9. **Dotenv for Kotlin (Variables d'environnement)**

10. **MockWebServer (Tests HTTP)**

11. **Kotlin Coroutines**

12. **Glide (Image Loading)**

13. **Activity & Fragment KTX**

14. **Legacy Support Library**

15. **RecyclerView**

16. **MPAndroidChart (Charts & Graphs)**

17. **JUnit & AndroidX Test Libraries**

## Ressources

- Documentation API 42 : <https://api.intra.42.fr/apidoc>
- Tutoriels Kotlin pour Android :
  - [Android Developers](https://developer.android.com/kotlin)
  - [YouTube : Coding in Flow](https://www.youtube.com/channel/UC_Fh8kvtkVPkeihBs42jGcA)

## Auteur

Projet réalisé par Thomas GRIFFITH dans le cadre du cursus 42.