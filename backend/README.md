# Paperly Backend

Paperly is a Spring Boot application for managing subjects, questions, and automated question-paper generation.

## Features
- JWT-based authentication for admin and faculty users
- Subject management with validation and safe deletion rules
- Question bank CRUD and filtering by subject, topic, and difficulty
- Exact-mark paper generation based on topic allocations
- Randomized question selection with bounded subset-sum search
- PDF and Word (DOCX) export for generated papers
- Admin-only user creation, role management, and deletion
- Admin-only subject management; faculty and admin question management
- Database seeding for default admin and faculty accounts

## Local setup
1. Ensure Java 17 and Maven are installed.
2. Create the MySQL schema:

```powershell
cmd.exe /c "mysql -u root -p < .\src\main\resources\db\schema-mysql.sql"
```

Then create the application account in MySQL, choosing a strong password:

```sql
CREATE USER 'paperly_app'@'localhost' IDENTIFIED BY 'replace-with-a-strong-password';
GRANT ALL PRIVILEGES ON paperly.* TO 'paperly_app'@'localhost';
```

3. Configure the MySQL password and JWT secret before starting. The default URL, username, and driver target local MySQL:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/paperly"
$env:DB_USERNAME = "paperly_app"
$env:DB_PASSWORD = "replace-with-a-strong-password"
$env:DB_DRIVER = "com.mysql.cj.jdbc.Driver"
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-bytes-long"
$env:PAPERLY_ADMIN_USERNAME = "admin"
$env:PAPERLY_ADMIN_PASSWORD = "change-this-before-first-start"
$env:PAPERLY_FACULTY_USERNAME = "faculty"
$env:PAPERLY_FACULTY_PASSWORD = "change-this-before-first-start"
```

4. Start the app:

```bash
mvn spring-boot:run
```

5. Sign in with the seeded user account or the credentials you configured.

## API overview
- `POST /api/auth/login` -> sign in and receive a JWT token
- `GET /api/subjects` -> list all subjects
- `POST /api/subjects` -> create a subject
- `PUT /api/subjects/{id}` -> update a subject
- `DELETE /api/subjects/{id}` -> delete a subject
- `GET /api/questions` -> list/filter questions
- `POST /api/questions` -> add a question
- `PUT /api/questions/{id}` -> update a question
- `DELETE /api/questions/{id}` -> delete a question
- `POST /api/papers/generate` -> generate a paper
- `GET /api/papers` -> list generated papers
- `GET /api/papers/{id}/pdf` -> download paper as PDF
- `GET /api/papers/{id}/docx` -> download paper as a Word document
- `GET /api/admin/users` -> list users (admin only)
- `POST /api/admin/users` -> create an admin/faculty account (admin only)
- `PUT /api/admin/users/{id}/role` -> change a user's role (admin only)
- `DELETE /api/admin/users/{id}` -> delete a user without generated papers (admin only)

## Default seeded users
The app seeds one administrator and one faculty account when the user table is empty. Configure usernames and passwords with the environment variables above **before the first start**. Changing seed variables later does not change existing database accounts. Never use development credentials or the default JWT secret in production.

For a fresh local MySQL database, the sample sign-ins are `admin` / `Admin@123` and `faculty` / `Faculty@123`. Change these seed credentials before the first start when using real or shared data. H2 is configured for automated tests only; it is not used by the running application.

## Automated MySQL backups

`scripts\backup-mysql.ps1` creates a consistent SQL dump in `backups\` and prunes dumps older than 30 days. It uses the encrypted `paperly-admin` MySQL login path currently configured on this Windows account. This avoids exposing the password, but gives the scheduled task administrator database privileges. For production, use a dedicated read-only MySQL backup account and change the login path in both backup scripts.

Install the daily 02:00 backup task (or choose another 24-hour time):

```powershell
.\scripts\install-daily-backup-task.ps1 -Time "02:00"
```

The task runs as the current Windows user and only runs while that user is signed in. Run a backup manually with `.\scripts\backup-mysql.ps1`. Dumps contain question content and user password hashes; protect the backup folder and keep an off-machine copy for disaster recovery.
