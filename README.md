<div align="center">

# Paperly

### Question Paper Generator

Build balanced, randomized question papers from a categorized question bank.
Manage subjects and users, configure topic-wise marks, and export ready-to-use papers.

**Angular** · **Spring Boot** · **Java 17** · **MySQL 8**

</div>

---

## Overview

Paperly is a full-stack workspace for faculty and administrators. It organizes questions by subject, topic, difficulty, and marks, then generates papers that match the selected criteria.

## What you can do

| Area | Capabilities |
|---|---|
| **Question bank** | Add, edit, search, filter, and remove questions by subject, topic, and difficulty. |
| **Subjects** | Create and maintain the subjects that organize your question library. |
| **Paper builder** | Generate randomized papers with exact total marks, topic allocations, and optional difficulty selection. |
| **Paper archive** | Review your generated papers and download PDF or Word (DOCX) copies. |
| **Administrator dashboard** | Create faculty and administrator accounts, change user roles, and manage access. |
| **Access control** | Protect user management for administrators and safeguard the last administrator account. |

The sample question library includes **at least 15 questions for every seeded topic** across Mathematics, Physics, and Literature.

## Technology

| Layer | Stack |
|---|---|
| Frontend | Angular, TypeScript, HTML, CSS |
| Backend | Java 17, Spring Boot, Spring Security |
| Persistence | MySQL 8, Spring Data JPA, Hibernate |
| Authentication | JWT, BCrypt |
| Document exports | iText PDF, Apache POI DOCX |

## Requirements

- Java 17 and Maven
- Node.js and npm
- MySQL Server 8.x

## Run locally

Run the backend and frontend in separate PowerShell terminals from the project root.

### Backend

```powershell
cd .\backend
mvn spring-boot:run
```

### Frontend

```powershell
cd .\frontend
npm install
npm start
```



## Workspace pages

| Page | Route | Access |
|---|---|---|
| Sign in | `/login` | All users |
| Overview | `/overview` | Faculty and administrators |
| Question bank | `/questions` | Faculty and administrators |
| Subjects | `/subjects` | Faculty and administrators |
| Generate paper | `/generate` | Faculty and administrators |
| Paper history | `/papers` | Faculty and administrators |
| User management | `/users` | Administrators only |

### Administrator dashboard

Use **User management** to view accounts, add faculty or administrator users with a temporary password, change another user's role, and remove eligible accounts. Administrators cannot remove or change their own account role; the last administrator cannot be demoted or deleted. Accounts with generated papers are protected from deletion.

## Sample question bank

The starter data covers three subjects and nine topics:

| Subject | Topics |
|---|---|
| Mathematics | Algebra · Calculus · Geometry |
| Physics | Mechanics · Electricity · Optics |
| Literature | Drama · Prose · Poetry |

Each listed topic has at least 15 sample questions. The sample-data script is safe to run again and avoids inserting duplicate questions.

## Project layout

```text
backend/
├── src/main/java/com/paperly/       Spring Boot API and application services
├── src/main/resources/
│   ├── application.properties       Application and MySQL configuration
│   └── db/
│       ├── schema-mysql.sql         MySQL schema
│       └── sample-questions-mysql.sql Sample question bank
├── src/test/                        Backend tests
└── scripts/                         MySQL backup utilities

frontend/
├── src/app/                         Angular UI, routing, auth, and API client
└── proxy.conf.json                  Development API proxy (port 2715)
```

## Validate the project

### Backend tests

```powershell
cd .\backend
mvn test
```

### Frontend tests and production build

```powershell
cd .\frontend
npm test -- --watch=false --browsers=ChromeHeadless
npm run build
```

## Documentation

- [Backend setup and API details](./backend/README.md)
- [Frontend setup and routes](./frontend/README.md)
