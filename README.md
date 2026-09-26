# Momentum

A habit-tracking system with smart streak-recovery — because missing one day shouldn't wipe out your progress. Built with React, Java, Spring Boot & JWT.

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://momentum-habit-streak-tracker-1.onrender.com/)
[![React](https://img.shields.io/badge/React%2019-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/)
[![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

### 🌐 Live Application
Experience the live application deployed on Render:
👉 **[https://momentum-habit-streak-tracker-1.onrender.com/](https://momentum-habit-streak-tracker-1.onrender.com/)**

---

## Overview

**Momentum** is a full-stack habit-tracking application that rethinks the all-or-nothing streak model most trackers use. Instead of resetting your streak to zero after a single missed day, Momentum applies smarter recovery logic that reflects how habits actually work in real life — with built-in analytics, dark-mode modern UI, and customizable streaks to keep users engaged.

Built with a unified architecture: a modern **React + Vite** frontend baked directly inside a **Java 21 / Spring Boot** executable service.

---

## Tech Stack

* **Frontend**: React 19, Vite, Vanilla CSS with custom design tokens, Lucide-style SVG icons.
* **Backend**: Java 21, Spring Boot 4 / 3, Spring Data JPA, Spring Security with stateless JWT.
* **Database**: MySQL / Cloud MySQL with embedded H2 fallback for instant zero-configuration deployments.
* **Deployment & Containerization**: Multi-stage Docker container hosted on Render.

---

## Features

- 🔥 **Smart Streak Recovery** — Missing a day doesn't reset your progress to zero; recovery logic accounts for real-life gaps instead of punishing an all-or-nothing streak.
- ✅ **Full Habit CRUD** — Create, customize (frequency, target completions, color icons), edit, and delete habits.
- 📊 **Analytics Dashboard** — View completion rates, current streaks, longest streaks, and completion history.
- 🔐 **JWT Authentication & RBAC** — Secure user registration, login, and stateless JWT token handling.
- 📱 **Responsive Modern UI** — Polished dark-mode aesthetic with smooth animations, modal dialogs, and instant toast notifications.
- 🚀 **Unified Single-Port Deployment** — React SPA and Spring Boot REST APIs packaged into a single container with client-side SPA routing.

---

## Getting Started

### Prerequisites

- Java 17+ or 21
- Maven 3.6+
- Node.js 18+ (for frontend development)

### Local Development

1. **Clone the repository**:
   ```bash
   git clone https://github.com/CodeWithJatinSh/Momentum---Habit-Streak_Tracker-.git
   cd Momentum---Habit-Streak_Tracker-
   ```

2. **Run the React Frontend** (in development mode with hot reload):
   ```bash
   npm run dev
   ```
   Available at `http://localhost:3000` (automatically proxies API calls to `localhost:8080`).

3. **Run the Spring Boot Backend**:
   ```bash
   ./mvnw spring-boot:run
   ```
   API & Unified server available at `http://localhost:8080`.

4. **Build Unified Production JAR** (bundles React inside Spring Boot):
   ```bash
   npm run build:spring
   ./mvnw clean package -DskipTests
   java -jar target/momentum-0.0.1-SNAPSHOT.jar
   ```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and receive a JWT token |
| GET | `/api/dashboard` | Get user dashboard metrics and summary |
| GET | `/api/habits` | Get all habits for the logged-in user |
| POST | `/api/habits` | Create a new habit |
| PUT | `/api/habits/{id}` | Update an existing habit |
| DELETE | `/api/habits/{id}` | Delete a habit |
| POST | `/api/habits/{id}/log` | Log a habit completion for today |
| GET | `/api/habits/{id}/streak` | Get current streak and recovery status |
| GET | `/api/habits/{id}/logs` | Get completion log history |

---

## Project Structure

```
Momentum/
├── Dockerfile                  # Multi-stage container build (Node + Maven + JRE)
├── package.json                # Monorepo proxy scripts
├── pom.xml                     # Spring Boot & dependencies
├── frontend/                   # React 19 + Vite SPA
│   ├── src/
│   │   ├── components/         # Dashboard, Modals, Navbar, Hero, Icons
│   │   ├── api.js              # Centralized API service & JWT session management
│   │   ├── index.css           # Custom dark theme & responsive styles
│   │   └── App.jsx             # Root application shell
│   └── vite.config.js          # Vite configuration with proxy & outDir
└── src/                        # Spring Boot Java Backend
    └── main/
        ├── java/com/momentum/habittracker/
        │   ├── Config/         # SPA routing WebConfig
        │   ├── Controller/     # Auth, Habit, HabitLog, Dashboard controllers
        │   ├── DTO/            # Request & response data transfer objects
        │   ├── Entities/       # JPA entities (User, Habit, HabitLog)
        │   ├── Repository/     # Spring Data JPA repositories
        │   ├── Security/       # JWT token filter & SecurityConfig
        │   └── Service/        # Business logic & streak recovery engine
        └── resources/
            ├── application.properties
            └── static/         # Bundled React production assets
```

---

## Roadmap

- [x] Core habit CRUD
- [x] Smart streak-recovery logic engine
- [x] JWT authentication & user sessions
- [x] React frontend UI integration
- [x] Analytics dashboard & streak visualization
- [x] Dockerization & cloud deployment on Render

---

## Author

**Jatin Sohanvi (Chinku)**  
Backend & Full-Stack Developer | Java & Spring Boot  
📧 [jatin.msc.cs@gmail.com](mailto:jatin.msc.cs@gmail.com)
