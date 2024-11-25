# One Page Application with Search Functionality

This project is a Maven-based parent project consisting of two child modules: a backend and a frontend.
The backend is a Spring Boot (version 3.3.4) application with Spring Security integration.
The frontend is built with Angular (version 18).
The application provides a recipe search functionality that triggers an OpenSearch search and saves the search request in a PostgreSQL database.
Additionally, the project includes a login, registration, homepage (search view), and admin view of all searches.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Database Configuration](#database-configuration)
- [OpenSearch Configuration](#opensearch-configuration)
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
### Setting up PostgreSQL with Docker without using Docker-Compose
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
   
### Setting up PostgreSQL with Docker with using Docker-Compose
When running `docker-compose up -d` in the `backend` directory, the postgres service defined in `docker-compose.yaml` will start as well. More information about the docker-compose configuration are provided at [OpenSearch Configuration](#opensearch-configuration).

### Switching to different Database
If using a different database (e.g., MySQL), update your `pom.xml` accordingly and adjust the connection properties in `application.properties`.

### Prepopulated Database
The database after starting the application will include 4 tables: users, user_roles, roles, and search-history.
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

## OpenSearch Configuration
The project includes an OpenSearch configuration for indexing and searching data.

### Setting up OpenSearch & Docker Compose
1. Pull the OpenSearch Image:
    ```bash
   docker pull opensearchproject/opensearch:latest
2. Pull the OpenSearch Dashboards Image
    ```bash
   docker pull opensearchproject/opensearch:latest
   
3. Make sure you have docker-compose installed (default for DockerDesktop).
4. Move to `backend/src/main/resources` and create an `.env`:
    ```bash
   OPENSEARCH_INITIAL_ADMIN_PASSWORD=Iphos_Task_123!
   POSTGRES_USER=admin
    POSTGRES_PASSWORD=password
    POSTGRES_DB=firstDatabase
5. Make sure the values in `index_recipes.py`, `application.properties` and `docker-compose.yaml` match this password or use the .env var.
6. Run the cluster in the `backend`directory:
    ```bash
   docker-compose up -d
   ```
   To stop the cluster run:
    ```bash
   docker-compose stop
   ```
   To remove the cluster run:
    ```bash
   docker-compose down
   ```
### Indexing Data with Python
1. Dowload the `.zip` containing the `.json` file for the recipes from https://www.kaggle.com/datasets/hugodarwood/epirecipes, extract it and move the `json` file to the `backend` directory.
2. Ensure you have Python installed and added to your system's PATH.
3. In the `backend` directory, install the necessary dependencies by running:
    ```bash
   pip install pip install opensearch-py
3. Run the following command to index the data:
    ```bash
   python index_recipes.py
   ```
    This script will create an index called 'recipes' and index the JSON data automatically.

### Indexing Data with Integration Test
1. Dowload the `.zip` containing the `.json` file for the recipes from https://www.kaggle.com/datasets/hugodarwood/epirecipes, extract it and move the `.json` file to the `backend/src/test/resources` directory.
2. Open the file `OpenSearchIntegrationTest` in directory `backend/src/test/java/org/topalovic/backend/OpenSearchTests`.
3. Run the `bulkIndexRecipesFromJson()` Test or the whole class to index the data from the .json file.

### Indexing Data with OpenSearch Dashboards
1. Navigate to `http://localhost:5601` to the 'Dev Tools' of the OpenSearch Dashboard.
2. Create an Index:
    ```bash
   PUT /recipes
   {
       "mappings": {
           "properties": {
               "title": { "type": "text" },
               "desc": { "type": "text" },
               "date": { "type": "date" },
               "categories": { "type": "keyword" },
               "ingredients": { "type": "keyword" },
               "directions": { "type": "keyword" },
               "calories": { "type": "float" },
               "fat": { "type": "float" },
               "protein": { "type": "float" },
               "rating": { "type": "float" },
               "sodium": { "type": "float" }
           }
       }
   }
3. Index recipes according to this structure:
    ```bash
   POST /recipes/_doc/123
    {
       "title": "A very nice Vegan dish",
       "desc": "A beautiful description of the recipe",
       "date": "2015-05-01T04:00:00.000Z",
       "categories": [
            "Vegan",
            "Tree Nut Free",
            "Soy Free",
            "No Sugar Added"
       ],
       "ingredients": [
            "list",
            "of",
            "ingredients"
       ],
       "directions": [
            "list",
            "of",
            "steps",
            "to prepare the dish"
       ],
       "calories": 32.0,
       "fat": 1.0,
       "protein": 1.0,
       "rating": 5.0,
       "sodium": 959.0
    }
3. Test the search with:
    ```bash
   GET /recipes/_search
    {
        "query": {
            "match": {
                "title": "A very nice Vegan dish"
            }
        }
    }

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

- API Endpoints:
  - POST `/api/auth/login`
  - POST `/api/auth/register`
  - POST `/api/auth/signout`
- Steps:
  - Users can log in, log out or register using the provided API endpoints.
  - Upon successful login, a JWT token is returned to the user.
  - The user can use the JWT token for subsequent authenticated requests.

### Search History Admin Management

- API Endpoints:
  - GET `/api/search-history/all`
  - POST `/api/search-history/groupedByUser`
- Steps:
  - Users with the role 'ADMIN' can access these endpoints to retrieve the entire search history and grouped by user.

### Recipe Search by Title & Search Items Management

- API Endpoint:
  - POST `/api/search-request/search-by-text`
- Steps:
    - Users can search for recipes by title using the provided API endpoint.
    - The query is done with AUTO-`fuzziness` and uses the `and`-operator.
    - The search term(s) get(s) saved into the search history repository.
    - The API endpoint returns all recipes that contain or are similar to the search term in their title.

- API Endpoint:
  - POST `api/search-request/search-by-calories`
- Steps:
  - Users can search for recipes by range of calories as well as sort them in ascending or descending order.
  - The API endpoint returns all recipes that fall into the specified calories-range.

- API Endpoint:
    - POST `api/search-request/search-by-sodium`
- Steps:
    - Users can search for recipes by range of sodium as well as sort them in ascending or descending order.
    - The API endpoint returns all recipes that fall into the specified sodium-range.

### Role-Based Access Control

- Spring Security:
  - Users with the "USER" role can access the main page.
  - Users with the "ADMIN" role can access the admin view.
  - Access to protected resources is controlled using Spring Security's role-based access control.

### Unit and Integration Tests

- Backend Tests:
  - The backend application provides unit and integration tests. 
  - Unit tests cover individual components and services. 
  - Integration tests ensure the proper functioning of the application as a whole and can be used for database population as well as opensearch indexing.


## Frontend Functionality

### Login View

- API Endpoint:
  - POST `/api/auth/signin`
- Steps:
    - Navigate to the navigation view ('/login').
    - Enter username and password (example: "user1" & "password1").
    - Click the "Login' button.
    - If successful, you will be redirected to the main page ('/').
    - If already logged in, you will automatically be redirected to the main page.

### Registration View

- API Endpoint:
  - POST `/api/auth/signup`
- Steps:
    - Navigate to the registration view ('/register').
    - Enter a new username, email and password.
    - Click the "Register" button.
    - If successful, you will be redirected to the login view.
    - If already logged in, you will automatically be redirected to the main page.
    - Logout is possible by clicking the "Logout" button.

### Main Page

- API Endpoint:
  - POST `/api/search-request/search-by-text`
- Steps:
    - Navigate to the main page ('/').
    - Enter one or more search terms in the input field.
    - Click the "Search" button.
    - The search term will be added to the database and displayed in the user's search history.
    - All recipes with a title containing the search term get displayed.

### Admin View

- API Endpoint:
  - GET `/api/search-history/groupedByUser`
- Steps:
    - Navigate to the admin view ('/admin/search-history').
        - accessible only to users with the "ADMIN" role
    - All search items from the database grouped by users will be displayed in the admin view.



## Learner's Note

I am learning Spring Boot and Angular with this project and am still in the process of development. This project has a basic implementation of Spring Security and will be further improved to enhance security.