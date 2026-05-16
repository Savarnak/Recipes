# Recipe Management REST API

An enterprise-style Spring Boot backend for managing recipe data, built to demonstrate practical Java backend engineering skills: REST API design, layered architecture, persistence, validation, exception handling, testing, documentation, and containerized deployment.

## Overview

The application ingests a bundled JSON dataset into MySQL on startup and exposes APIs to create, browse, filter, sort, update, and delete recipes.

## Architecture

```text
Controller -> Service -> Repository -> MySQL
                  |
                  -> DTO mapping / validation / business rules
```

### Key design choices

- Controllers are thin and return explicit `ResponseEntity` responses.
- Services contain business logic and logging.
- Repositories encapsulate persistence queries.
- DTOs separate API contracts from JPA entities.
- Global exception handling provides a consistent error model.

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Jackson
- Lombok
- Bean Validation
- Spring Boot Actuator
- springdoc-openapi / Swagger UI
- JUnit 5, MockMvc, Mockito, H2
- Docker / Docker Compose

## Setup

### Local

1. Create a MySQL database named `recipe_db`
2. Set environment variables:

```bash
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

3. Run:

```bash
./mvnw spring-boot:run
```

### Docker

```bash
docker compose up --build
```

## Useful URLs

- API base: `http://localhost:8080/recipes`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/recipes` | Paginated recipe list with filtering and sorting |
| GET | `/recipes/{id}` | Fetch recipe by ID |
| GET | `/recipes/top?limit=5` | Fetch top-rated recipes |
| GET | `/recipes/parsed` | Preview source JSON records |
| POST | `/recipes` | Create recipe |
| PUT | `/recipes/{id}` | Update recipe |
| DELETE | `/recipes/{id}` | Delete recipe |

### Query parameters for `GET /recipes`

- `cuisine`
- `page`
- `size`
- `sortBy`
- `direction`

Example:

```http
GET /recipes?cuisine=Indian&page=0&size=10&sortBy=rating&direction=desc
```

## Sample Request

```json
{
  "title": "Paneer Curry",
  "cuisine": "Indian",
  "rating": 4.7,
  "prep_time": 15,
  "cook_time": 30,
  "description": "Rich tomato-based curry",
  "nutrients": {
    "protein": "20g"
  },
  "serves": "4"
}
```

## Sample Validation Error

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "validationErrors": {
    "title": "Title is required",
    "rating": "Rating must not exceed 5"
  }
}
```

## Screenshots

Add screenshots here after running the app:

- Swagger UI
- Successful API call
- Validation error response
- Actuator health endpoint

## Testing

```bash
./mvnw test
```

Coverage includes:

- service-layer behavior
- controller endpoint behavior with MockMvc
- repository filtering behavior
- application context startup

## Resume-ready highlights

- Designed RESTful CRUD APIs with pagination, filtering, and sorting
- Implemented layered Spring Boot architecture with DTO separation
- Added validation, structured error handling, and production-style logging
- Used JPA/Hibernate for persistence and optimized startup ingestion with batching
- Added Swagger/OpenAPI documentation, Actuator health checks, and Docker support
- Wrote unit, web-layer, repository, and context tests
