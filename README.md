# Sale Vehicle

Ce projet est une application Spring Boot pour la gestion des ventes de véhicules. Il utilise PostgreSQL comme base de données et inclut des fonctionnalités telles que la gestion des paiements et la génération de factures PDF.

## Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

## Installation

1. Clonez le dépôt :

    ```bash
    git clone https://github.com/ms-teach1547/sale-vehicle.git
    cd sale-vehicle
    ```

2. Installez les dépendances Maven et compilez le projet :

    ```bash
    mvn clean install
    ```

## Configuration

Le fichier de configuration principal est [application.properties]. Voici les principales configurations :

```properties
spring.application.name=sale-vehicle
server.port=8081


# Configuration JPA / Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=0

# Configuration du téléchargement de fichiers
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
spring.web.resources.static-locations=file:./uploads/

# Logging
logging.level.org.springframework.security=DEBUG



### Pour se connecter 
_- entant que ADMIN
username : admin2
password : mahamat

- USER 
username : ms   
password : ms1547

-  USER (COMPANY)
username : entreprise
password : entreprise1