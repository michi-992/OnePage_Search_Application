# One Page Application with Search Functionality

This project is a Maven-based parent project consisting of two child modules: a backend and a frontend. The backend is a Spring Boot (version 3.3.4) application, while the frontend is built with Angular (version 17). The application for now only provides basic a search functionality where users can add search items to a databse and have them displayed.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Database Configuration](#database-configuration)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [API Endpoints](#api-endpoints)

## Prerequisites 
- Java: 23.0.1
- Maven: 3.9.9
- Node: v20.18.0
- npm: 10.8.2
- Angular: 18.2.9
- Spring Boot: 3.3.4
- Optional:
  - PostgreSQL: 17.0.1
  - Docker: 27.2.0
## Getting Started

To build and run the project:

1. Clone the GitHub repository:
   ```bash
   git clone https://github.com/michi-992/OnePage_Search_Application

2. Navigate to the project directory and build it:
    ```bash
    mvn clean install

This will compile and package both backend and frontend modules.

## Project Structure

- **Parent Project**  
  Contains the overall project structure and dependencies for both backend and frontend modules.

- **Backend**  
  A Spring Boot application (3.3.4) that provides REST APIs.

- **Frontend**  
  An Angular application (18) that provides the user interface for the one-page application.

## Database Configuration
### Setting up PostgreSQL with Docker
1. Install Docker: Download and install Docker from https://www.docker.com/.
2. Pull the PostgrSQL Image:
    ```bash
   docker pull postgres
3. Run PostgreSQL Container
    ```bash
   docker run --name some-postgres -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=mydatabase -p 5432:5432 -d postgres
    ```
   - myUser: database username
   - mysecretpassword: database password
   - mydatabase: database name
### Configuring the Application
1. Edit `application.properties` in `backend/src/main/resources/application.properties`:
    ```bash
   spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
    spring.datasource.username=myuser
    spring.datasource.password=mysecretpassword
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
### Switching to different Database
If using a different database (e.g., MySQL), update your `pom.xml` accordingly and adjust the connection properties in `application.properties`.

## Backend Setup

1. **Running the Backend**  
   To start the backend, navigate to the backend directory and run:

   ```bash
   cd backend
   mvn spring-boot:run

The backend runs on http://localhost:8080 by default.

## Frontend Setup

1. **Running the Backend**  
   To start the backend, navigate to the backend directory and run:

   ```bash
   cd frontend/src/main/web
   ng serve

The frontend will be available at http://localhost:4200, unless otherwise specified.

## API Endpoints

The backend provides the following API endpoints

- GET '/searchItems'
    -  Returns all search items saved in the database as a List.
    -  Test with
       ```bash
        curl http://localhost:8080/searchItems

- POST '/searchItems'
    -  Adds a new search item in the database.
    -  Test with
       ```bash
        curl -X POST -H "Content-Type: application/json" -d '{"searchTerm": "your_search_term"}' http://localhost:8080/searchItems/add
