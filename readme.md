# Sale Vehicle

Ce projet est une application Spring Boot pour la gestion des ventes de véhicules. Il utilise PostgreSQL comme base de données et inclut des fonctionnalités telles que la gestion des paiements et la génération de factures PDF.

## Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 12 ou supérieur

## Installation

1. Clonez le dépôt :

    ```bash
    git clone https://github.com/ms-teach1547/sale-vehicle.git
    cd sale-vehicle
    ```

2. Configurez la base de données PostgreSQL :

    - Créez une base de données PostgreSQL nommée `sale_vehicle_db`.
    - Mettez à jour les informations de connexion à la base de données dans le fichier [application.properties]

3. Installez les dépendances Maven et compilez le projet :

    ```bash
    mvn clean install
    ```

## Configuration

Le fichier de configuration principal est [application.properties]. Voici les principales configurations :

```properties
spring.application.name=sale-vehicle
server.port=8081

# Configuration de la base de données PostgreSQL
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/sale_vehicle_db
spring.datasource.username=postgres
spring.datasource.password=teacher1547.psql
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

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