# Interview Preparation Guide

This document covers key topics from the SmartTask AI project that you can discuss in job interviews.

---

## 1. JWT Authentication

**What is JWT?**
JWT (JSON Web Token) is a compact, URL-safe token format used for securely transmitting information between parties.

**How does authentication work in this project?**

1. User submits credentials to `/api/auth/login`
2. Server validates credentials using Spring Security's AuthenticationManager
3. On success, JwtHelper.generateToken() creates a signed token
4. Token contains: username, issued time, expiration time
5. Client stores token in localStorage
6. Every request includes token in Authorization header
7. JwtAuthenticationFilter validates token on each request

**Key Interview Points:**
- Tokens are signed using HMAC-SHA256
- Token expiration set to 24 hours (86400000 ms)
- Passwords are hashed using BCrypt (not stored in plain text)
- Stateless authentication - no server-side session storage

**Code Reference:**
- JwtHelper.java - Token generation and validation
- JwtAuthenticationFilter.java - Request filter
- SecurityConfig.java - Security configuration

---

## 2. Spring Security Configuration

**What security measures are implemented?**

1. JWT-based stateless authentication
2. BCrypt password encoding
3. CORS configuration for frontend
4. Role-based access control
5. Protected endpoints require valid JWT

**Key Interview Points:**
- Custom UserDetailsService loads user from database
- Password encoder uses BCrypt with strength 10
- Filter chain executes before Spring's UsernamePasswordAuthenticationFilter
- Public endpoints: /api/auth/**, /ws/**, swagger docs

**Code Reference:**
- SecurityConfig.java - Main security configuration
- CustomUserDetailsService.java - User loading logic

---

## 3. Database Design

**Entity Relationships:**

User (1) -> Task (Many)

**User Table:**
- id (Primary Key)
- username (Unique)
- email (Unique)
- password (BCrypt hash)
- fullName
- enabled
- createdAt, updatedAt

**Task Table:**
- id (Primary Key)
- title, description
- status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- priority (LOW, MEDIUM, HIGH, URGENT)
- category (WORK, PERSONAL, etc.)
- dueDate, completedAt
- aiSummary, aiSuggestion
- position (for ordering)
- user_id (Foreign Key)

**Key Interview Points:**
- One-to-Many relationship between User and Task
- Lazy fetching for better performance
- Custom JPA queries for filtering
- Pagination with Spring Data

---

## 4. AI Integration

**How is AI integrated?**

1. OpenAI GPT-4o API integration via Spring AI
2. ChatClient used to send prompts and receive responses
3. AI automatically suggests:
   - Category when creating tasks
   - Priority based on task content
   - Summaries of all tasks
   - Productivity analysis

**Key Interview Points:**
- Prompt engineering for consistent results
- Error handling when AI API fails
- Fallback to default values on failure
- Async processing for AI calls

**Code Reference:**
- AIService.java - All AI functionality

---

## 5. REST API Design

**Best Practices Implemented:**

1. Proper HTTP methods:
   - POST for creating resources
   - GET for retrieving
   - PUT for full updates
   - PATCH for partial updates
   - DELETE for removing

2. Status codes:
   - 200 OK
   - 201 Created
   - 204 No Content
   - 400 Bad Request
   - 401 Unauthorized
   - 404 Not Found

3. Pagination for list endpoints
4. DTOs for request/response
5. Validation on input fields
6. Global exception handling

**Key Interview Points:**
- Resource-based URL structure (/api/tasks)
- Query parameters for filtering/sorting
- Consistent response format
- Error handling strategy

---

## 6. Redis Caching

**What is cached?**

- Redis configured for caching (ready for implementation)
- Cache TTL: 1 hour

**Key Interview Points:**
- Spring's @EnableCaching annotation
- RedisTemplate for custom serialization
- Cache invalidation strategy (not implemented but ready)

---

## 7. WebSocket Real-time Features

**How does it work?**

1. STOMP protocol over SockJS
2. Server pushes updates to specific users
3. Topics: /queue/tasks, /queue/tasks/created, /queue/tasks/deleted

**Key Interview Points:**
- WebSocketConfig.java configures STOMP
- SimpMessagingTemplate for server-to-client messaging
- User-specific queues for private notifications

---

## 8. Frontend Architecture

**Tech Stack:**
- React 19 with TypeScript
- Zustand for state management
- Vite for build tooling
- Tailwind CSS for styling

**Key Interview Points:**
- Component-based architecture
- Custom hooks pattern (via Zustand stores)
- Axios interceptors for auth headers
- Drag-and-drop with @hello-pangea/dnd
- Responsive design with Tailwind

---

## 9. Testing

**What tests exist?**

- Unit tests for AuthService using Mockito
- Test configuration uses H2 in-memory database

**Key Interview Points:**
- JUnit 5 for testing framework
- Mockito for mocking dependencies
- Test-driven development approach
- Test coverage for critical paths

---

## 10. Docker & Deployment

**What's containerized?**

- PostgreSQL database
- Redis cache
- Spring Boot backend
- React frontend (nginx)

**Key Interview Points:**
- Multi-stage Docker builds
- Docker Compose for orchestration
- Environment variables for configuration
- Health checks for dependencies

---

## Common Interview Questions

**Q: How do you handle security in this application?**
A: We use JWT tokens with Spring Security. Passwords are hashed with BCrypt. All endpoints except login/register require a valid token in the Authorization header.

**Q: How does the AI integration work?**
A: When a task is created without a category, we call OpenAI's API with a prompt describing the task. The AI responds with a suggested category. Similar process for priority suggestions.

**Q: What database queries do you use?**
A: We use Spring Data JPA with custom queries. For example, findOverdueTasks uses a JPQL query to find tasks past their due date that aren't completed.

**Q: How do you handle errors?**
A: GlobalExceptionHandler catches all exceptions and returns consistent error responses. We handle validation errors, authentication errors, and runtime exceptions differently.

**Q: Why did you choose this tech stack?**
A: Spring Boot is the industry standard for enterprise Java applications. React 19 with TypeScript provides type safety. JWT is the standard for stateless authentication. Redis and PostgreSQL are battle-tested technologies.

---

## What to Emphasize in Interviews

1. **Full-stack capabilities** - You built both backend and frontend
2. **Modern Java** - Using Java 21 features
3. **AI integration** - Shows awareness of modern trends
4. **Security** - JWT, BCrypt, Spring Security
5. **Best practices** - REST API, DTOs, validation, testing
6. **DevOps** - Docker, docker-compose
7. **Real-time features** - WebSocket implementation
