# Mini Task Management System

Spring Boot backend for task management with JWT authentication, role-based access (`ADMIN`, `USER`), and MySQL persistence.

## What This Project Includes

- Task CRUD APIs
- Task filtering by `status` and `priority`
- Task pagination and sorting
- JWT-based authentication (`/auth/register`, `/auth/login`)
- Role-based endpoint protection with Spring Security
- Admin user-management APIs
- First-run default admin bootstrap
- Global exception handling and request validation

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security + JWT (`jjwt`)
- MySQL
- Lombok
- Jakarta Validation

## Project Structure

```text
src/main/java/com/example/mini_task
├── config
│   ├── DefaultAdminInitializer.java
│   ├── MethodSecurityConfig.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── controller
│   ├── AdminUserController.java
│   ├── AuthController.java
│   └── TaskController.java
├── dto
│   ├── auth
│   │   ├── AuthResponse.java
│   │   ├── LoginRequest.java
│   │   └── RegisterRequest.java
│   ├── TaskRequestDTO.java
│   └── TaskResponseDTO.java
├── entity
│   ├── Task.java
│   └── User.java
├── exception
├── repo
├── security
└── service
    ├── AdminUserService.java
    ├── AuthenticationService.java
    ├── TaskService.java
    └── impl
```

## Roles and Access

- `USER`
  - Access task endpoints
  - Can manage own tasks only
- `ADMIN`
  - Access task endpoints
  - Can view all tasks in `GET /api/v1/tasks`
  - Can access all `/api/v1/admin/**` endpoints

> Note: In current implementation, single-task operations (`GET /tasks/{id}`, `PUT`, `DELETE`, `PATCH /complete`) are still owner-scoped and return not found for non-owners.

## API Documentation

- Detailed API reference: `API_DOCUMENTATION.md`

## Quick Start

### 1) Prerequisites

- Java 21+
- MySQL 8+

### 2) Create database

```sql
CREATE DATABASE `mini-task-db`;
```

### 3) Apply schema (recommended)

Use the schema file to create all tables, constraints, and indexes:

```powershell
mysql -u root -p < src/main/resources/db/mysql/schema.sql
```

### 4) Configure properties

Update `src/main/resources/application.properties` for your environment, especially:

- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret`
- `app.bootstrap.admin.*`

### 5) Run the app (Windows)

```powershell
.\mvnw.cmd spring-boot:run
```

### 6) Build and test (Windows)

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

## Auth Flow

1. Register user: `POST /api/v1/auth/register`
2. Login user: `POST /api/v1/auth/login`
3. Use returned token in header:

```http
Authorization: Bearer <token>
```

## Main Endpoints

### Public

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Tasks (`USER`, `ADMIN`)

- `POST /api/v1/tasks`
- `GET /api/v1/tasks`
- `GET /api/v1/tasks/{id}`
- `PUT /api/v1/tasks/{id}`
- `DELETE /api/v1/tasks/{id}`
- `PATCH /api/v1/tasks/{id}/complete`

### Admin (`ADMIN` only)

- `POST /api/v1/admin/users/register`
- `GET /api/v1/admin/users`
- `GET /api/v1/admin/users/{userId}`
- `PATCH /api/v1/admin/users/{userId}/role?role=ADMIN|USER`
- `DELETE /api/v1/admin/users/{userId}`
- `GET /api/v1/admin/users/me`

## Default Admin Bootstrap

On application startup, `DefaultAdminInitializer` creates one admin user only if:

- `app.bootstrap.admin.enabled=true`
- there is currently no admin in DB
- configured bootstrap email does not already exist as a non-admin user

Current defaults from `application.properties`:

- Email: `admin@gmail.com`
- Password: `Admin@123`

Change these values before production use.

## CORS

CORS is enabled for `/api/**` and allows:

- Origins: `*`
- Methods: `GET, POST, PUT, DELETE, PATCH, OPTIONS`
- Headers: `*`

Security also permits preflight `OPTIONS` requests.

## Error Handling

- Validation and application exceptions are handled by `GlobalExceptionHandler`
- Security auth/authorization failures are handled by Spring Security handlers in `SecurityConfig`

Typical statuses:

- `200`, `201`, `204`
- `400` validation/business rule errors
- `401` unauthenticated
- `403` forbidden
- `404` resource not found

## Notes

- JPA schema generation is currently `spring.jpa.hibernate.ddl-auto=update`
- JWT expiry is configured via `jwt.expiration` (default currently 24h)
- Admin safeguards implemented:
  - cannot demote last admin
  - cannot delete last admin
  - admin cannot delete own account
