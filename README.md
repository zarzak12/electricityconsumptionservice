# Electricity Consumption Service

Ce projet est une application Spring Boot qui permet de suivre et d'analyser la consommation d'électricité. Les utilisateurs peuvent télécharger des fichiers CSV contenant les données de consommation et obtenir des statistiques et des tendances.

## Fonctionnalités

- Télécharger des fichiers CSV contenant les données de consommation d'électricité.
- Afficher la consommation journalière et mensuelle avec des tendances.
- Calculer et afficher les prix quotidiens et mensuels basés sur la consommation.
- Afficher le montant annuel estimé de la consommation d'électricité.

## Prérequis

- Docker
- Docker Compose

## Installation

1. Clonez le dépôt

```bash
git clone https://github.com/votre-utilisateur/electricity-consumption-service.git
cd electricity-consumption-service
```
2. Construisez et exécutez l'application avec Docker
```bash
docker-compose up --build
```
3. Ouvrez votre navigateur et accédez à l'adresse suivante :

http://localhost:8080/consumption

## Format du fichier CSV
Le fichier CSV doit suivre le format suivant :
```bash
R�capitulatif de ma consommation
Date de consommation;Consommation (kWh);Nature de la donn�e
01/01/2024;12.34;Facturée
02/01/2024;11.56;Estimée
...
```

## Structure du projet
ConsumptionController.java : Contrôleur principal qui gère les requêtes HTTP.
ConsumptionService.java : Service qui contient la logique métier pour traiter les données de consommation.
resources/templates/consumption.html : Template Thymeleaf pour afficher les données de consommation.

## Développement
Pour contribuer à ce projet, suivez ces étapes :

## Forkez le dépôt.
Créez une branche pour votre fonctionnalité (git checkout -b ma-nouvelle-fonctionnalite).
Commitez vos changements (git commit -am 'Ajoute une nouvelle fonctionnalité').
Poussez votre branche (git push origin ma-nouvelle-fonctionnalite).
Créez une Pull Request.

## Auteur
Votre Derz - Votre Profil GitHub

## Licence
Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.

Cette configuration vous permet de construire et d'exécuter votre application Spring Boot dans un conteneur Docker, facilitant ainsi le déploiement et la gestion de l'application.
