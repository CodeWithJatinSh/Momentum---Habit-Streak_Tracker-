# Momentum

A habit-tracking API with smart streak-recovery — because missing one day shouldn't wipe out your progress. Built with Java, Spring Boot & JWT.

## Overview

**Momentum** is a backend habit-tracking system that rethinks the all-or-nothing streak model most trackers use. Instead of resetting your streak to zero after a single missed day, Momentum applies smarter recovery logic that reflects how habits actually work in real life — with built-in analytics and reminders to keep users engaged.

Built with Java, Spring Boot, Spring Data JPA/Hibernate, MySQL, and JWT-based authentication.

## Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

## Features

- 🔥 **Smart Streak Recovery** — Missing a day doesn't reset your progress to zero; recovery logic accounts for real-life gaps instead of punishing an all-or-nothing streak
- ✅ **Habit CRUD** — Create, update, and manage daily/weekly habits
- 📊 **Analytics** — Track completion rates, streak history, and trends over time
- 🔔 **Reminders** — Configurable notifications to keep habits on track
- 🔐 **JWT Authentication & RBAC** — Secure, role-based access to user data
- 🗄️ **Persistent Storage** — MySQL with Spring Data JPA/Hibernate for reliable data management

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/<your-username>/momentum.git
   cd momentum
   ```

2. Configure your database connection in `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/momentum_db
   spring.datasource.username=<your-username>
   spring.datasource.password=<your-password>
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build the project
   ```bash
   mvn clean install
   ```

4. Run the application
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Endpoints

> Endpoint list will be filled in as controllers are built.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and receive a JWT |
| GET | `/api/habits` | Get all habits for the logged-in user |
| POST | `/api/habits` | Create a new habit |
| PUT | `/api/habits/{id}` | Update an existing habit |
| DELETE | `/api/habits/{id}` | Delete a habit |
| POST | `/api/habits/{id}/log` | Log a habit completion for today |
| GET | `/api/habits/{id}/streak` | Get current streak and recovery status |
| GET | `/api/analytics` | Get completion analytics and trends |

## Project Structure

```
momentum/
├── src/
│   ├── main/
│   │   ├── java/com/momentum/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── security/
│   │   │   └── config/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Roadmap

- [ ] Core habit CRUD
- [ ] Streak-recovery logic engine
- [ ] JWT auth & RBAC
- [ ] Reminder scheduling
- [ ] Analytics dashboard endpoints
- [ ] React frontend integration


## Author

**Jatin Sohanvi (Chinku)**
Backend Developer | Java & Spring Boot
📧 jatin.msc.cs@gmail.com
