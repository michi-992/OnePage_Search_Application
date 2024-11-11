# One Page Application with Search Functionality

This project is a Maven-based parent project consisting of two child modules: a backend and a frontend.
The backend is a Spring Boot (version 3.3.4) application with Spring Security integration.
The frontend is built with Angular (version 18).
The application provides a search functionality where users can add search items to a database and have them displayed.
Additionally, the project includes a login, registration, homepage (search view), and admin view of all searches.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Database Configuration](#database-configuration)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Backend Functionality](#backend-functionality)
- [Frontend Functionality](#frontend-functionality)
- [Learner's Note](#learners-note)

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

### Prepopulated Database
The database after starting the application will include 4 tables: users, user_roles, roles, and searchItems.
The tables will be prepopulated:
1. **roles**  
   - user
   - admin
2. **users**  
   - normal user (username = "user1", password = "password1")
   - an admin (username = "admin", password = "password2")
3. **user_roles**  
   - user1: user
   - admin: user & admin
4. **search_items**  
   - two search items for the 2 users each

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


## Backend Functionality
### User Authentication and Authorization

- API Endpoints: POST '/auth/login', POST '/auth/register', POST '/auth/signout'
- Steps:
  - Users can log in, log out or register using the provided API endpoints.
  - Upon successful login, a JWT token is returned to the user.
  - The user can use the JWT token for subsequent authenticated requests.

### Search Items Management

- API Endpoints: GET '/searchItems/user/{username}', POST '/searchItems/user/{username}/add'
- Steps:
  - Users can fetch their search items using the GET '/searchItems' endpoint.
  - Users can add new search items using the POST '/searchItems' endpoint.
  - The search items are stored in the database and associated with the user.

### Role-Based Access Control

- Spring Security:
  - Users with the "USER" role can access the main page.
  - Users with the "ADMIN" role can access the admin view.
  - Access to protected resources is controlled using Spring Security's role-based access control.

### Unit and Integration Tests

- Backend Tests:
  - The backend application provides unit and integration tests. 
  - Unit tests cover individual components and services. 
  - Integration tests ensure the proper functioning of the application as a whole and can be used for database population.

## Frontend Functionality

### Login View

- API Endpoint: POST '/auth/signin'
- Steps:
    - Navigate to the navigation view ('/login').
    - Enter username and password (example: "user1" & "password1").
    - Click the "Login' button.
    - If successful, you will be redirected to the main page ('/').
    - If already logged in, you will automatically be redirected to the main page.

### Registration View

- API Endpoint: POST '/auth/signup'
- Steps:
    - Navigate to the registration view ('/register').
    - Enter a new username, email and password.
    - Click the "Register" button.
    - If successful, you will be redirected to the login view.
    - If already logged in, you will automatically be redirected to the main page.
    - Logout is possible by clicking the "Logout" button.

### Main Page

- API Endpoint: GET '/searchItems/user/{username}', POST '/searchItems/user/{username}/add'
- Steps:
    - Navigate to the main page ('/').
    - Enter a search term in the input field.
    - Click the "Search" button.
    - The search term will be added to the database and displayed in the user's search history.
#
### Admin View

- API Endpoint: GET '/searchItems/all'
- Steps:
    - Navigate to the admin view ('/admin/search-history').
        - accessible only to users with the "ADMIN" role
    - All search items from the database will be displayed in the admin view.



## Learner's Note

I am learning Spring Boot and Angular with this project and am still in the process of development. This project has a basic implementation of Spring Security and will be further improved to enhance security.