# Mini Task Management System - API Documentation

## Overview
This is a secure REST API for a Mini Task Management System built with Spring Boot, Spring Security, and JWT authentication. The system allows users to manage their tasks with features like filtering, pagination, and sorting.

## Authentication

### JWT Token
- All endpoints except `/api/v1/auth/register` and `/api/v1/auth/login` require JWT authentication
- Include the token in the Authorization header: `Authorization: Bearer <token>`
- Token expiration time: 24 hours (86400000 ms)

---

## Authentication Endpoints

### 1. Register User
**Endpoint:** `POST /api/v1/auth/register`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Validation Rules:**
- First name: 2-50 characters (required)
- Last name: 2-50 characters (required)
- Email: Valid email format (required)
- Password: 6-100 characters (required)
- Password confirmation must match password

**Error Responses:**
- `400 Bad Request` - Validation errors or password mismatch
- `400 Bad Request` - Email already registered

---

### 2. Login User
**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Error Responses:**
- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Invalid email or password

---

## Task Management Endpoints

### 1. Create Task
**Endpoint:** `POST /api/v1/tasks`

**Authentication:** Required (Bearer Token)

**Request Body:**
```json
{
  "title": "Complete Project",
  "description": "Finish the Mini Task Management System",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-03-15T18:00:00"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Complete Project",
  "description": "Finish the Mini Task Management System",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-03-15T18:00:00",
  "createdAt": "2026-03-10T10:40:00",
  "updatedAt": "2026-03-10T10:40:00"
}
```

**Validation Rules:**
- Title: Required (non-blank)
- Status: TODO, IN_PROGRESS, or DONE (required)
- Priority: LOW, MEDIUM, or HIGH (required)
- Due Date: Valid date format (required)

**Error Responses:**
- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Invalid or missing token

---

### 2. Get All Tasks
**Endpoint:** `GET /api/v1/tasks`

**Authentication:** Required (Bearer Token)

**Query Parameters:**
- `page` (optional, default: 0) - Page number (0-indexed)
- `size` (optional, default: 10) - Page size
- `sortBy` (optional, default: dueDate) - Sort field
- `sortDirection` (optional, default: asc) - Sort direction (asc/desc)
- `status` (optional) - Filter by status (TODO, IN_PROGRESS, DONE)
- `priority` (optional) - Filter by priority (LOW, MEDIUM, HIGH)

**Example:**
```
GET /api/v1/tasks?page=0&size=10&sortBy=priority&sortDirection=desc&status=TODO&priority=HIGH
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Complete Project",
      "description": "Finish the system",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2026-03-15T18:00:00",
      "createdAt": "2026-03-10T10:40:00",
      "updatedAt": "2026-03-10T10:40:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "number": 0,
  "size": 10,
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token

---

### 3. Get Single Task
**Endpoint:** `GET /api/v1/tasks/{id}`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
- `id` - Task ID

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Complete Project",
  "description": "Finish the system",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-03-15T18:00:00",
  "createdAt": "2026-03-10T10:40:00",
  "updatedAt": "2026-03-10T10:40:00"
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token
- `404 Not Found` - Task not found or unauthorized access

---

### 4. Update Task
**Endpoint:** `PUT /api/v1/tasks/{id}`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
- `id` - Task ID

**Request Body:**
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2026-03-20T18:00:00"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2026-03-20T18:00:00",
  "createdAt": "2026-03-10T10:40:00",
  "updatedAt": "2026-03-10T10:50:00"
}
```

**Error Responses:**
- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Invalid or missing token
- `404 Not Found` - Task not found or unauthorized access

---

### 5. Delete Task
**Endpoint:** `DELETE /api/v1/tasks/{id}`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
- `id` - Task ID

**Response (204 No Content):** Empty response body

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token
- `404 Not Found` - Task not found or unauthorized access

---

### 6. Mark Task as Completed
**Endpoint:** `PATCH /api/v1/tasks/{id}/complete`

**Authentication:** Required (Bearer Token)

**Path Parameters:**
- `id` - Task ID

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Complete Project",
  "description": "Finish the system",
  "status": "DONE",
  "priority": "HIGH",
  "dueDate": "2026-03-15T18:00:00",
  "createdAt": "2026-03-10T10:40:00",
  "updatedAt": "2026-03-10T10:51:00"
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token
- `404 Not Found` - Task not found or unauthorized access

---

## Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request successful (no response body) |
| 400 | Bad Request - Validation errors or invalid input |
| 401 | Unauthorized - Invalid, missing, or expired token |
| 404 | Not Found - Resource not found or unauthorized access |
| 500 | Internal Server Error - Server error |

---

## Error Response Format

```json
{
  "status": 400,
  "message": "Field validation error",
  "timestamp": "2026-03-10T10:40:00",
  "path": "/api/v1/tasks"
}
```

---

## Task Status Values

- `TODO` - Task not started
- `IN_PROGRESS` - Task is being worked on
- `DONE` - Task is completed

---

## Task Priority Values

- `LOW` - Low priority
- `MEDIUM` - Medium priority
- `HIGH` - High priority

---

## Security Features

1. **JWT Authentication** - All protected endpoints require a valid JWT token
2. **Password Encryption** - User passwords are encrypted using BCrypt
3. **User Isolation** - Users can only access their own tasks
4. **Token Expiration** - Tokens expire after 24 hours
5. **Authorization Checks** - Server validates user ownership of resources

---

## Example Usage

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

### 2. Login with email and password
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Create a new task (using token from login)
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "title": "Complete Project",
    "description": "Finish the system",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2026-03-15T18:00:00"
  }'
```

### 4. Get all tasks with filters
```bash
curl -X GET "http://localhost:8080/api/v1/tasks?page=0&size=10&status=TODO&priority=HIGH&sortBy=dueDate&sortDirection=asc" \
  -H "Authorization: Bearer <token>"
```

### 5. Update a task
```bash
curl -X PUT http://localhost:8080/api/v1/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "title": "Updated Title",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "dueDate": "2026-03-20T18:00:00"
  }'
```

### 6. Mark task as completed
```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/1/complete \
  -H "Authorization: Bearer <token>"
```

### 7. Delete a task
```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/1 \
  -H "Authorization: Bearer <token>"
```

---

## Configuration

Configuration is stored in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/mini-task-db
spring.datasource.username=root
spring.datasource.password=Ijse@123

# JWT
jwt.secret=MyVerySecureSecretKeyForJWTTokenGenerationAndValidationPurposesOnly123456789
jwt.expiration=86400000
```

---

## Architecture

### Layered Structure

```
com.example.mini_task
├── controller/
│   ├── AuthController
│   └── TaskController
├── service/
│   ├── AuthenticationService (Interface)
│   ├── TaskService (Interface)
│   └── impl/
│       ├── AuthenticationServiceImpl
│       ├── TaskServiceImpl
│       └── UserDetailsServiceImpl
├── repo/
│   ├── UserRepository
│   └── TaskRepository
├── entity/
│   ├── User
│   └── Task
├── dto/
│   ├── auth/
│   │   ├── RegisterRequest
│   │   ├── LoginRequest
│   │   └── AuthResponse
│   ├── TaskRequestDTO
│   └── TaskResponseDTO
├── security/
│   ├── JwtTokenProvider
│   ├── JwtAuthenticationFilter
│   └── SecurityContextUtil
├── exception/
│   ├── GlobalExceptionHandler
│   ├── ResourceNotFoundException
│   ├── AuthenticationException
│   └── ApiException
├── config/
│   └── SecurityConfig
└── MiniTaskApplication
```

### Design Patterns

- **Layered Architecture** - Clean separation of concerns
- **DTO Pattern** - Data Transfer Objects for API requests/responses
- **Service Layer** - Business logic separated from controllers
- **Repository Pattern** - Data access abstraction
- **Exception Handling** - Global exception handler using @RestControllerAdvice
- **Security** - JWT-based stateless authentication

---

## Dependencies

- Spring Boot 4.0.3
- Spring Data JPA
- Spring Security
- JJWT 0.12.3
- MySQL Connector
- Lombok
- Jakarta Validation

