# Projet de réseau

## Avant de lancer le serveur

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

## Lancer le .bat

Le fichier.bat est situé dans le dossier bindist/bin/run.bat


## Informations

Projet réalisé par les étudiants MIAGE en 2021:

- BEIRAO Camille
- BOUCHÉ Valentine
- COSSIN Alain