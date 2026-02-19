# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints (except `/api/auth/register` and `/api/auth/login`) require authentication.
Include the JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

---

## Endpoints

### 1. Authentication

#### Register User
```
POST /api/auth/register
```

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

#### Login User
```
POST /api/auth/login
```

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

---

### 2. Tasks

#### Get All Tasks (Paginated)
```
GET /api/tasks
```

**Query Parameters:**
| Parameter | Default | Description |
|-----------|---------|-------------|
| page | 0 | Page number |
| size | 10 | Page size |
| sortBy | createdAt | Sort field |
| sortDir | desc | Sort direction (asc/desc) |

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Complete project",
      "description": "Finish the task management app",
      "status": "PENDING",
      "priority": "HIGH",
      "category": "WORK",
      "dueDate": "2026-03-01T17:00:00",
      "completedAt": null,
      "aiSummary": null,
      "aiSuggestion": null,
      "position": 0,
      "userId": 1,
      "createdAt": "2026-02-15T10:00:00",
      "updatedAt": "2026-02-15T10:00:00"
    }
  ],
  "totalPages": 1,
  "totalElements": 1,
  "size": 10,
  "number": 0
}
```

#### Get Task By ID
```
GET /api/tasks/{id}
```

**Response:**
```json
{
  "id": 1,
  "title": "Complete project",
  "description": "Finish the task management app",
  "status": "PENDING",
  "priority": "HIGH",
  "category": "WORK",
  "dueDate": "2026-03-01T17:00:00",
  "completedAt": null,
  "aiSummary": null,
  "aiSuggestion": null,
  "position": 0,
  "userId": 1,
  "createdAt": "2026-02-15T10:00:00",
  "updatedAt": "2026-02-15T10:00:00"
}
```

#### Create Task
```
POST /api/tasks
```

**Request Body:**
```json
{
  "title": "New task",
  "description": "Task description",
  "status": "PENDING",
  "priority": "MEDIUM",
  "category": "WORK",
  "dueDate": "2026-03-01T17:00:00"
}
```

Note: If category or priority is not provided, AI will automatically suggest appropriate values.

**Response:** Returns the created task object.

#### Update Task
```
PUT /api/tasks/{id}
```

**Request Body:**
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "category": "WORK"
}
```

**Response:** Returns the updated task object.

#### Delete Task
```
DELETE /api/tasks/{id}
```

**Response:** 204 No Content

#### Get Tasks By Status
```
GET /api/tasks/status/{status}
```

**Status Values:** PENDING, IN_PROGRESS, COMPLETED, CANCELLED

**Response:** Array of tasks

#### Get Tasks By Category
```
GET /api/tasks/category/{category}
```

**Category Values:** GENERAL, WORK, PERSONAL, HEALTH, LEARNING, SHOPPING, FINANCE, SOCIAL

**Response:** Array of tasks

#### Get Tasks By Priority
```
GET /api/tasks/priority/{priority}
```

**Priority Values:** LOW, MEDIUM, HIGH, URGENT

**Response:** Array of tasks

#### Get Overdue Tasks
```
GET /api/tasks/overdue
```

**Response:** Array of overdue tasks

#### Get All Tasks Ordered
```
GET /api/tasks/ordered
```

**Response:** Array of all tasks ordered by position

#### Update Task Status
```
PATCH /api/tasks/{id}/status?status={status}
```

**Example:**
```
PATCH /api/tasks/1/status?status=COMPLETED
```

**Response:** Returns the updated task object.

#### Update Task Position
```
PATCH /api/tasks/{id}/position?position={position}
```

**Example:**
```
PATCH /api/tasks/1/position?position=5
```

---

### 3. AI Features

#### Get AI Summary
```
POST /api/tasks/ai/summarize
```

**Response:**
```json
{
  "summary": "You have 5 pending tasks. 2 are high priority and due soon..."
}
```

#### Get Task Suggestion
```
POST /api/tasks/{id}/ai/suggestion
```

**Response:**
```json
{
  "suggestion": "Break this task into smaller subtasks to make progress faster."
}
```

#### Get Productivity Analysis
```
POST /api/tasks/ai/productivity
```

**Response:**
```json
{
  "analysis": "You've completed 70% of your tasks this week. Consider prioritizing your 3 high-priority items..."
}
```

---

## Error Responses

All error responses follow this format:

```json
{
  "timestamp": "2026-02-15T10:00:00",
  "message": "Error message here",
  "status": 400
}
```

**Common HTTP Status Codes:**

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## WebSocket

Connect to WebSocket at: `/ws`

**Endpoints:**
- Subscribe to `/user/{userId}/queue/tasks` for real-time task updates
- Subscribe to `/user/{userId}/queue/tasks/created` for new task notifications
- Subscribe to `/user/{userId}/queue/tasks/deleted` for task deletion notifications
