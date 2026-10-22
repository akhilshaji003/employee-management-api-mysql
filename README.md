# Employee Management REST API

A small assessment-ready REST API built with Java 17, Spring Boot, Spring Web, Spring Data JPA, Bean Validation, MySQL and OpenAPI. It follows a layered architecture and includes pagination, search/filtering, structured exception responses, a Postman collection and a basic integration test.

## Features

- Create, list, retrieve, update and delete employees
- DTO-based request/response contract; JPA entity is not exposed directly
- Validation for required fields, email format, salary and field lengths
- Duplicate-email protection (case-insensitive)
- Consistent JSON error responses via `@RestControllerAdvice`
- Pagination and sorting
- Search by first or last name; filter by active status or department
- Swagger UI and OpenAPI JSON
- MySQL runtime database and H2-based automated test profile

## Architecture

```text
src/main/java/com/example/employeemanagement/
├── config/       # OpenAPI configuration
├── controller/   # HTTP endpoints and request validation
├── dto/          # Request, response and error DTOs
├── entity/       # JPA entity and persistence mapping
├── exception/    # Domain exceptions and centralized error handler
├── repository/   # Spring Data JPA repository
├── service/      # Business logic and transaction boundaries
└── EmployeeManagementApplication.java
```

Controller handles HTTP concerns, service owns business rules, repository handles persistence, entity maps to the database, and DTOs define the API contract. Constructor injection is used throughout.

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use your IDE's Maven integration)
- MySQL 8.0+

## Database setup

Create a MySQL database and application user. Run the following SQL as a MySQL administrator (or use `db/init.sql`):

```sql
CREATE DATABASE IF NOT EXISTS employee_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'employee_app'@'localhost' IDENTIFIED BY 'replace-with-a-strong-password';
GRANT ALL PRIVILEGES ON employee_management.* TO 'employee_app'@'localhost';
FLUSH PRIVILEGES;
```

If the user/database already exist, skip the corresponding statement. For any non-local environment, use a strong password and do not commit credentials.

The defaults are configured in `src/main/resources/application.yml`. Override them with environment variables:

```bash
export DB_URL='jdbc:mysql://localhost:3306/employee_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='employee_app'
export DB_PASSWORD='your-local-password'
```

PowerShell equivalent:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/employee_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="employee_app"
$env:DB_PASSWORD="your-local-password"
```

For assessment simplicity, Hibernate uses `ddl-auto: update` to create/update the schema at startup. In production, prefer versioned migrations such as Flyway or Liquibase and set Hibernate DDL mode to `validate`.

## Run the application

From the project root:

```bash
mvn clean test
mvn spring-boot:run
```

Or package and run:

```bash
mvn clean package
java -jar target/employee-management-api-1.0.0.jar
```

The API listens on `http://localhost:8080`.

## API documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
- Postman collection: `postman/Employee-Management-API.postman_collection.json`

Import the Postman collection and use its `baseUrl` and `employeeId` variables.

## Endpoints

Base path: `/api/v1/employees`

| Method | Endpoint | Description | Success |
|---|---|---|---|
| POST | `/api/v1/employees` | Create employee | `201 Created` |
| GET | `/api/v1/employees` | List employees (paged) | `200 OK` |
| GET | `/api/v1/employees/{id}` | Get employee | `200 OK` |
| PUT | `/api/v1/employees/{id}` | Replace employee details | `200 OK` |
| DELETE | `/api/v1/employees/{id}` | Delete employee | `204 No Content` |

### List query parameters

- `page` (default `0`), zero-based
- `size` (default `10`, max `100`)
- `sort` (default `id,asc`), e.g. `salary,desc`
- `search` matches first name or last name
- `active` accepts `true` or `false`
- `department` filters by department

Example:

```http
GET /api/v1/employees?page=0&size=10&sort=lastName,asc&active=true
```

When multiple filter parameters are supplied, the service applies one filter using this precedence: `active`, then `department`, then `search`. This keeps the example intentionally small and easy to explain.

## Sample requests and responses

### Create employee

Request:

```http
POST /api/v1/employees
Content-Type: application/json
```

```json
{
  "firstName": "Asha",
  "lastName": "Nair",
  "email": "asha.nair@example.com",
  "department": "Engineering",
  "salary": 65000.00,
  "active": true
}
```

Response (`201 Created`, example timestamps/ID):

```json
{
  "id": 1,
  "firstName": "Asha",
  "lastName": "Nair",
  "email": "asha.nair@example.com",
  "department": "Engineering",
  "salary": 65000.00,
  "active": true,
  "createdAt": "2026-10-22T10:15:30",
  "updatedAt": "2026-10-22T10:15:30"
}
```

### Get employee

```http
GET /api/v1/employees/1
```

Response: the same `EmployeeResponse` representation.

### Validation error

Request with an invalid email or missing required field returns `400 Bad Request`:

```json
{
  "timestamp": "2026-10-22T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/employees",
  "validationErrors": {
    "email": "Email must be valid",
    "firstName": "First name is required"
  }
}
```

### Employee not found

`GET /api/v1/employees/99999` returns `404 Not Found`:

```json
{
  "timestamp": "2026-10-22T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 99999",
  "path": "/api/v1/employees/99999",
  "validationErrors": {}
}
```

## Design decisions and trade-offs

- **DTOs:** prevent persistence details from leaking into the API and provide a place for validation.
- **Service layer:** business rules and transaction boundaries are not placed in controllers.
- **Repository:** Spring Data JPA removes boilerplate CRUD persistence code.
- **Validation:** Bean Validation is used at the request boundary.
- **Exception handling:** a centralized advice maps known failures to meaningful HTTP status codes and a stable response shape.
- **Email uniqueness:** checked in the service for a friendly message and constrained in the database for integrity. Under concurrent requests, the database constraint remains the final safeguard.
- **Pagination:** prevents the list endpoint from returning an unbounded result set.
- **Testing:** the integration test uses an isolated in-memory H2 database so it does not require a local MySQL server.
- **Scope:** the project deliberately avoids adding authentication, complex role management or deployment infrastructure because this is a focused coding assessment.

## Run tests

```bash
mvn test
```

Tests use the `test` profile in `src/test/resources/application-test.yml`, which configures H2 in MySQL compatibility mode.

## Suggested GitHub submission steps

1. Extract this ZIP.
2. Review the code and run the tests locally.
3. Update this README with any changes you make and add your own repository URL after creating the GitHub repository.
4. Initialize and push:

```bash
git init
git add .
git commit -m "Build employee management REST API"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repository>.git
git push -u origin main
```

Before the review, be prepared to explain the controller/service/repository separation, DTO validation, transaction boundaries, uniqueness constraints, pagination and the error-handling flow.
