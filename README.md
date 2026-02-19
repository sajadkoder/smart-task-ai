# SmartTask AI

An AI-powered task management application with a modern Kanban board interface. SmartTask AI helps you organize tasks, prioritize work, and boost productivity with intelligent automation.

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- H2 Database (default) / PostgreSQL (production)
- JWT Authentication
- WebSocket
- Redis (optional, for caching)

### Frontend
- React 18
- Vite
- Tailwind CSS
- React Beautiful DnD
- Axios
- React Router

## Features

### Core Features
- User Authentication - Secure JWT-based authentication system
- Task Management - Full CRUD operations on tasks
- Kanban Board - Drag-and-drop task organization with columns: To Do, In Progress, Done
- AI-Powered Intelligence:
  - Automatic task categorization based on keywords
  - Smart priority suggestions
  - Productivity analytics and insights
  - Task deadline recommendations
- Real-time Updates - WebSocket support for live updates
- RESTful API - Complete backend API for all operations

### Categories & Priorities
- Categories: Work, Personal, Health, Learning, Shopping, Finance, Social, General
- Priorities: Urgent, High, Medium, Low
- Statuses: Pending, In Progress, Completed

## Quick Start

### Prerequisites
- Java 21 or higher
- Node.js 18 or higher
- Maven 3.8+

### Clone the Repository
```bash
git clone https://github.com/sajadkoder/smart-task-ai.git
cd smart-task-ai
```

### Backend Setup

1. Navigate to the project directory:
```bash
cd SmartTaskAI
```

2. Build the application:
```bash
mvn clean package -DskipTests
```

3. Run the backend:
```bash
java -jar target/smarttask-ai-backend-1.0.0.jar
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

### Access the Application
Open your browser and navigate to `http://localhost:5173`

## Project Structure

```
SmartTaskAI/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smarttask/
│   │   │       ├── config/         # Configuration classes
│   │   │       ├── controller/     # REST controllers
│   │   │       ├── dto/           # Data Transfer Objects
│   │   │       ├── entity/         # JPA entities
│   │   │       ├── repository/     # Data repositories
│   │   │       ├── security/       # Security & JWT
│   │   │       ├── service/        # Business logic
│   │   │       └── SmartTaskAIApplication.java
│   │   └── resources/
│   │       └── application.yml    # App configuration
│   └── test/                       # Test files
├── target/                         # Build output
└── pom.xml                         # Maven dependencies

frontend/
├── src/
│   ├── components/        # React components
│   ├── pages/            # Page components
│   ├── services/         # API services
│   ├── App.jsx           # Main app
│   ├── main.jsx          # Entry point
│   └── index.css         # Global styles
├── index.html
├── package.json
├── vite.config.js
└── tailwind.config.js
```

## Configuration

### Backend (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:smarttask
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 8080

jwt:
  secret: your-secret-key
  expiration: 86400000
```

### Environment Variables (Optional)

- `DB_HOST` - Database host (for PostgreSQL)
- `DB_PORT` - Database port
- `DB_NAME` - Database name
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT secret key
- `REDIS_HOST` - Redis host (for caching)

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/profile` - Get user profile

### Tasks
- `GET /api/tasks` - Get all user tasks
- `POST /api/tasks` - Create new task
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PUT /api/tasks/{id}/status` - Update task status

### AI Features
- `GET /api/tasks/ai/summary` - Get task summary
- `GET /api/tasks/ai/insights` - Get productivity insights
- `POST /api/tasks/ai/suggest` - Get task suggestions

## AI Service

The AI service uses a rule-based approach to provide:

1. Task Categorization - Analyzes task titles and descriptions to assign categories
2. Priority Suggestions - Recommends priority based on keywords and category
3. Deadline Recommendations - Suggests deadlines based on task type
4. Productivity Analysis - Provides insights on task completion rates

### Keyword Categories
- Work: meeting, project, deadline, client, presentation, report
- Health: exercise, workout, gym, doctor, medicine
- Learning: study, learn, course, book, tutorial
- Shopping: buy, shop, grocery, purchase
- Finance: bill, payment, invoice, tax, budget

## Security

- JWT-based authentication
- Password hashing with BCrypt
- CORS configuration
- Request validation
- Rate limiting (optional)

## Testing

Run backend tests:
```bash
mvn test
```

## Deployment

### Docker (Optional)

Create a Dockerfile for the backend:
```dockerfile
FROM openjdk:21-jdk
WORKDIR /app
COPY target/smarttask-ai-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Production Notes

1. Change the JWT secret in production
2. Use PostgreSQL instead of H2 for production
3. Enable Redis for caching (optional)
4. Configure CORS for your frontend domain
5. Enable HTTPS/SSL

## License

This project is licensed under the MIT License.

## Author

**Sajad Koder**
- GitHub: [@sajadkoder](https://github.com/sajadkoder)
