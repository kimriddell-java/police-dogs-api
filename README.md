# Police Dogs API

REST API for managing police dogs, implemented using Java 21 and Micronaut.

The API supports creating, retrieving, updating and deleting dogs, together with pagination and filtering by name, breed and supplier.

## Technology

- Java 21
- Micronaut / Micronaut Data JDBC
- PostgreSQL
- Flyway
- MapStruct
- Maven
- JUnit 5 / Mockito
- Testcontainers

## Structure

The project uses a simple ports-and-adapters (hexagonal) structure:

```text
model        Domain model
application  Application services and ports
adapter      REST and PostgreSQL adapters
bootstrap    Application startup and dependency wiring
```

This keeps the domain and application logic independent of HTTP and persistence concerns. Constructor injection is used throughout.

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/dogs` | Create a dog |
| GET | `/api/dogs/{id}` | Get a dog |
| PUT | `/api/dogs/{id}` | Update a dog |
| DELETE | `/api/dogs/{id}` | Soft-delete a dog |
| GET | `/api/dogs/dogs` | List/filter dogs |
| GET | `/api/dogs/statuses` | Valid dog statuses |
| GET | `/api/dogs/leaving-reasons` | Valid leaving reasons |
| GET | `/api/dogs/genders` | Valid genders |

The list endpoint supports pagination:

```text
/api/dogs/dogs?page=0&size=10
```

and filtering by name, breed and supplier:

```text
/api/dogs/dogs?filter={"name":"Rex","breed":"German Shepherd"}
```

## Persistence

PostgreSQL is used for persistence and the schema is managed using Flyway.

Dogs are soft-deleted rather than physically removed. Soft-deleted dogs are retained in the database for audit purposes and are excluded from the list endpoint by default.

MapStruct is used for mapping between API, domain and persistence models.

## Testing

The project contains unit and integration tests using JUnit 5 and Mockito.

Integration tests use PostgreSQL 16 through Testcontainers, allowing the persistence layer, Flyway migrations and HTTP API to be tested against PostgreSQL rather than an in-memory database.

The tests cover the main API operations, pagination, filtering, validation, error handling and the exclusion of soft-deleted dogs from list results.

Run all tests from the project root:

```bash
mvn clean test
```

Docker must be running for the PostgreSQL Testcontainers integration tests.

## Running Locally

Requires Java 21, Maven and Docker.

Start PostgreSQL:

```bash
docker run --name police-dogs-postgres -e POSTGRES_DB=police_dogs -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16-alpine
```

Run the main method:

```text
org.example.dogs.bootstrap.Application
```

The API is available at:

```text
http://localhost:8080
```

Flyway creates the required database schema automatically when the application starts.

Once the PostgreSQL container has been created, it can subsequently be restarted with:

```bash
docker start police-dogs-postgres
```

## Example Postman Request

Create a dog using `POST /api/dogs` with `Content-Type: application/json`:

```json
{
  "name": "Rex",
  "breed": "German Shepherd",
  "badgeId": "PD-1234",
  "gender": "MALE",
  "birthDate": "2022-03-15",
  "dateAcquired": "2023-01-10",
  "currentStatus": "IN_SERVICE",
  "supplier": {
    "name": "Westfield Kennels"
  },
  "kennellingCharacteristics": [
    {
      "name": "Needs individual kennel"
    },
    {
      "name": "High energy"
    }
  ]
}
```