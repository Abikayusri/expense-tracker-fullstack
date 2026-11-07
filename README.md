# Expense Tracker (fullstack)

A small expense tracker used to practise fullstack development: a Java backend, a Vue web app, and a Kotlin Multiplatform mobile app, all talking to the same REST API.

| Part | Stack | Status |
|---|---|---|
| `backend/` | Java 21, Spring Boot 4, PostgreSQL, hexagonal architecture | Done |
| `web/` | Vue 3, TypeScript, Vite, TanStack Vue Query, Pinia, Tailwind CSS | Done |
| `mobile/` | Kotlin Multiplatform, Compose Multiplatform, Koin, Ktor | In progress |

## Features

- Register and log in (JWT, valid for 7 days)
- Income and expense categories, with an optional monthly budget for expense categories
- Transactions filtered by month and category, with paging
- Monthly summary: total income, total expense, balance, spending per category
- Push notification (Android, via FCM) the first time a category goes over its budget in a month

## Run the backend locally

Requirements: Docker and JDK 21.

```bash
cd backend && ./gradlew bootJar && cd ..
docker compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html. Call `POST /api/auth/register` or `/api/auth/login`, copy the `token`, then press **Authorize**.
- PostgreSQL is published on host port `5433` (database, user, and password are all `expense`).

Change the published ports with `APP_PORT` and `DB_PORT`, for example `APP_PORT=9100 docker compose up --build`.

Stop everything with `docker compose down`; add `-v` to also delete the database volume.

### Configuration

| Variable | Default | Purpose |
|---|---|---|
| `JWT_SECRET` | a development value | HS256 signing key, at least 32 bytes. Set your own outside local development. |
| `FIREBASE_CREDENTIALS_PATH` | empty | Path inside the container to a Firebase service-account JSON. When empty, pushes are only written to the log. |

To send real push notifications, download a service-account key from the Firebase console, save it as `secrets/firebase.json` (this folder is git-ignored), and start with:

```bash
FIREBASE_CREDENTIALS_PATH=/secrets/firebase.json docker compose up --build
```

## Run the web app

Requirements: Node 24 and the backend running (see above).

```bash
cd web
npm install
npm run dev
```

Open http://localhost:3100. The dev server forwards `/api` to `http://localhost:8080`; point it elsewhere with `API_TARGET`, for example `API_TARGET=http://localhost:9100 npm run dev`.

If `npm install` fails with `Cannot read properties of null (reading 'edgesOut')`, your npm has a known bug; update it with `npm install -g npm@latest` or run `npx npm@latest install`.

Web tests and checks:

```bash
cd web
npm run test:unit -- --run
npm run type-check
npm run lint
```

## Run the mobile app

Requirements: JDK 21, Android SDK (compileSdk 37), Xcode with an iOS simulator runtime, and [xcodegen](https://github.com/yonaskolb/XcodeGen) (`brew install xcodegen`).

Android (emulator or device):

```bash
cd mobile
./gradlew :androidApp:installDebug
```

iOS simulator:

```bash
cd mobile/iosApp
xcodegen generate
open iosApp.xcodeproj
```

Then run the `iosApp` scheme on an Apple-silicon simulator. The Xcode project is generated from `project.yml` and is not committed.

Mobile tests (Android host and iOS simulator):

```bash
cd mobile
./gradlew allTests
```

## Run the backend tests

```bash
cd backend && ./gradlew test
```

Integration tests start their own PostgreSQL through Testcontainers, so Docker must be running.

## Backend architecture

```
domain/            business rules in plain Java
application/
  port/in/         use cases called by the web layer
  port/out/        what the application needs from outside (DB, push, hashing, tokens)
  service/         use case implementations
adapter/
  in/web/          REST controllers and ProblemDetail error handling
  out/persistence/ JPA and JDBC adapters, Flyway migrations
  out/security/    BCrypt and JWT
  out/push/        FCM and logging push senders
config/            wiring
```

`ArchitectureTest` (ArchUnit) fails the build if a layer depends on one it shouldn't.
