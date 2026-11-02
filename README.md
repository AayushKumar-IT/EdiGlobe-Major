# Paperly Question Paper Generator

Paperly is a full-stack question-paper generator for administrators and faculty. It stores a categorized question bank, builds papers to exact marks using subject/topic/difficulty criteria, saves generated papers, and exports them to PDF or Word.

## Features

- JWT sign-in with administrator and faculty roles
- Admin user creation, role management, and deletion safeguards
- Subject and question management with subject/topic/difficulty filters
- Paper generation with exact total marks and per-topic allocations
- Randomized question selection from matching question-bank inventory
- User-specific paper history and PDF/DOCX downloads
- MySQL as the application database; H2 is used only by automated tests

## Requirements

- Java 17 and Maven
- Node.js and npm
- MySQL Server 8.x

## Run locally (Windows PowerShell)

Create the schema and application user in MySQL before starting the backend. The schema script creates the database and all tables:

```powershell
cmd.exe /c "mysql -u root -p < .\backend\src\main\resources\db\schema-mysql.sql"
```

Create the dedicated application account in MySQL, replacing the placeholder with your own strong password:

```sql
CREATE USER 'paperly_app'@'localhost' IDENTIFIED BY 'replace-with-a-strong-password';
GRANT ALL PRIVILEGES ON paperly.* TO 'paperly_app'@'localhost';
```

The schema is also checked and updated by Hibernate at application startup. Set the database password and JWT secret in the backend terminal before launching. The URL, username, and MySQL driver default to `jdbc:mysql://localhost:3306/paperly`, `paperly_app`, and `com.mysql.cj.jdbc.Driver`:

```powershell
$env:DB_PASSWORD = "replace-with-a-strong-password"
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-bytes-long"
```

Open two terminals from the project root.

Backend:

```powershell
cd .\backend
mvn spring-boot:run
```

Frontend:

```powershell
cd .\frontend
npm start
```

Open `http://localhost:4200`. The Angular development proxy sends `/api` requests to the backend at `http://localhost:2715` (the port configured in `backend/src/main/resources/application.properties`). Restart `npm start` after changing the proxy configuration.

The backend creates development accounts and sample subjects/questions on an empty database. Override seeded account settings before the first start using `PAPERLY_ADMIN_USERNAME`, `PAPERLY_ADMIN_PASSWORD`, `PAPERLY_FACULTY_USERNAME`, and `PAPERLY_FACULTY_PASSWORD`. The values are read only when accounts are first seeded. Do not use development credentials or the default JWT secret in production.

The default development sign-ins are `admin` / `Admin@123` and `faculty` / `Faculty@123`; change these before using real or shared data.

## Database behavior

The running application uses MySQL by default. Override the database connection with environment variables if your MySQL host, database name, or account differs:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/paperly"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-password"
$env:DB_DRIVER = "com.mysql.cj.jdbc.Driver"
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-bytes-long"
mvn spring-boot:run
```

The MySQL JDBC driver is included. Hibernate updates the schema using JPA mappings. H2 is configured only for automated tests and is not the application database. Configure database backups and a production migration strategy before deploying.

## Daily MySQL backups

The backend includes a PowerShell backup script that creates a consistent SQL dump and removes dumps older than 30 days. Backups are stored under `backend\backups\` and excluded from version control.

The backup script currently uses the existing encrypted `paperly-admin` MySQL login path. This makes setup possible without exposing a password, but grants the scheduled backup the administrator account's database privileges. For production, create a dedicated read-only MySQL backup user and change the `LoginPath` default in `backend\scripts\backup-mysql.ps1` and the login check in `backend\scripts\install-daily-backup-task.ps1` to its configured login path.

Install the daily 02:00 Windows Task Scheduler task:

```powershell
.\backend\scripts\install-daily-backup-task.ps1 -Time "02:00"
```

The task runs as the current Windows user and requires that user to be signed in, so the MySQL login path is available. To create and verify a backup immediately, run:

```powershell
.\backend\scripts\backup-mysql.ps1
```

To restore a dump, use a MySQL account with database creation privileges:

```powershell
cmd.exe /c "mysql --login-path=paperly-backup < .\backend\backups\paperly-YYYYMMDD-HHMMSS.sql"
```

Backups contain question content and user password hashes. Keep the backup folder on protected storage and copy backups to a separate, access-controlled location for disaster recovery. The scheduled task runs as the current Windows user and only while that user is signed in; the MySQL login path is encrypted for that user.

## Verification

```powershell
cd .\backend
mvn test
cd ..\frontend
npm test -- --watch=false --browsers=ChromeHeadless
npm run build
```

See [backend/README.md](./backend/README.md) for API details and [frontend/README.md](./frontend/README.md) for Angular CLI notes.
