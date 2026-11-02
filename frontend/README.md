# Frontend

The Paperly frontend is an Angular application for faculty and administrators. It connects to the Spring Boot API through the development proxy configured in `proxy.conf.json`.

## Run locally

```powershell
npm install
npm start
```

Open `http://localhost:4200`. Start the backend separately; see the project and backend README files for setup and development credentials.

## Application routes

- `/login` — sign in; authenticated users are redirected to the overview.
- `/overview` — workspace summary and recently generated papers.
- `/questions` — question bank search, filters, and CRUD.
- `/subjects` — subject management.
- `/generate` — create papers using subject, difficulty, total marks, and topic allocations.
- `/papers` — saved paper history and PDF/DOCX downloads.
- `/users` — admin-only user management.

Private routes require a valid session. The user-management route additionally requires the administrator role.

## Build and tests

```powershell
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```
