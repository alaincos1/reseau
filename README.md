# Projet de réseau

## Informations

### Présentation
Ce projet est un serveur from scratch. 
Les fonctionnalité principales de notre serveur :
- Traitement des requêtes GET 
- Gestion des exceptions courantes (400,401,403,404)
- Gestion multisite
- Gestion de plusieurs connexions en parallèle
- Protecte une ressource par une authentification basique

La fonctionnalité bonus choisie est la gestion du listing des répertoires (activable/désactivable).

### Généralités

Projet réalisé par les étudiants MIAGE en 2021 pour le projet de fin d'année du cours de Réseaux Informatiques avec Jérôme Bertrand.

- BEIRAO Camille
- BOUCHÉ Valentine
- COSSIN Alain

## Avant de lancer le serveur

### Récupérer le projet
Cloner le projet sur votre machine.
Vérifier que toutes les dépendances Maven sont fonctionnelles.
Si vous souhaitez recharger le projet, effectuer un `mvn clean install`.
Le fichier .bat est situé dans le dossier bindist/bin/run.bat

### Configurer le .properties

Le fichier .properties doit être dans le même dossier que le fichier .bat

Il contient les infos suivantes :
- port : le port de connexion par défaut
- repository : le chemin absolu vers le dossier racine où sont stockés tous les sites
- domain : la liste des noms de domaines qui redirige vers le dossier dans le repository

			exemple : verti.miage:verti
	
			verti.miage = nom de domaines dans etc/hosts
			
			verti = le répertoire racine du site
- repositoryMenu : l'activation ou non de la fonctionnalité bonus = gérer le listing des répertoires

			1 : activé
	
			0 : désactivé

### Configurer le etc/hosts

Ecrivez dans le fichier les même noms de domaine que dans le fichier .properties.

			Exemple :
			
			127.0.0.1	verti.miage

## Lancer le .bat

Le fichier.bat est situé dans le dossier bindist/bin/run.bat