# Repsy Package Repository API

This project is a **Spring Boot-based REST API** for a `.rep` package repository system. It allows users to upload `.rep` (ZIP) packages along with associated metadata in JSON format. 
This project demonstrates storage strategy abstraction, containerization, and multi-module Maven architecture.


## Project Summary

- Java 17 + Spring Boot 3.4.5
- Multi-module Maven project
- Supports two storage strategies
  * File system-based storage
  * MinIO-based object storage
- PostgreSQL used for metadata persistence
- Dockerized and published to DockerHub
- Storage libraries published to a private Maven repository on [Repsy.io](https://repsy.io)


## Modules

- **app**: Main Spring Boot application (REST API)
- **storage-core**: Module to import all storage libraries
- **storage-api**: Common interface for storage strategies
- **file-system-lib**: File system-based storage implementation
- **object-storage-lib**: MinIO-based object storage implementation

## Run with Docker Compose
  If you want to launch the application with all its required dependencies (PostgreSQL and MinIO), you can use the included docker-compose.yml file.
### Steps
 1. Make sure Docker is installed and running on your machine.

 2. Clone this repository and navigate into the project directory.
    
 3. Run the following command:
```
docker-compose up --build
```
This will start:

  - The Spring Boot application (app)

  - PostgreSQL (used for storing metadata)

  - MinIO (used for object storage if selected in config)

## Configuration
  Configuration changes can be done via application.properties or docker-compose.yml using environment variables.
  - app
    * storage_strategy
    * file_storage_location
    * minio_bucket-name
    * minio config
    * postgres config
  - minio
    * minio_root_user and minio_root_password
  - postgres
    * postgres_user and postgres_password
    * postgres_db

## Docker Container
  The container for the application is also available on DockerHub as barisuu/repsy-package-case-app
