# Task Manager & Weather Dashboard

A full-stack web application developed as part of a technical assessment for a developer role at Glucode.

The application combines task management with a location-aware weather dashboard. It demonstrates frontend development with Angular, backend API development with Spring Boot, relational database integration and external API consumption.

**Repository:** [github.com/KabeloM06/kabelo-maswanganye-senior-assessment](https://github.com/KabeloM06/kabelo-maswanganye-senior-assessment)

## Features

### Task Management
- Create tasks with a title and description.
- View existing tasks.
- Mark tasks as completed or incomplete.
- Remove tasks.
- Persist tasks in PostgreSQL.

### Weather Dashboard
- Automatically retrieve weather information using browser geolocation.
- Search for locations manually.
- Display the current temperature.
- Display sunrise and sunset times.
- Display the local time of the selected location.
- Fall back to Pretoria when geolocation is unavailable or permission is denied.

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend framework | Angular 22 |
| Frontend language | TypeScript |
| Frontend HTTP client | Angular HttpClient |
| Reactive programming | RxJS |
| Frontend state management | Angular Signals |
| Backend language | Java 21 |
| Backend framework | Spring Boot 4.1.2 |
| REST API | Spring Web MVC |
| Persistence standard | Jakarta Persistence (JPA) |
| ORM implementation | Hibernate |
| Data access | Spring Data JPA |
| Database | PostgreSQL |
| Database hosting | Neon |
| Validation | Spring Boot Validation |
| API documentation | Springdoc OpenAPI |
| Backend HTTP integration | Spring RestClient |
| Backend testing | Spring Boot testing dependencies |

## Architecture and Design Decisions

The application follows a frontend/backend separation.

Angular is responsible for presentation and client-side interactions, while Spring Boot handles business logic, data persistence and integration with external services.

### Frontend – Angular

The frontend uses Angular's standalone component architecture and feature-based organisation.

Features are grouped according to their responsibilities rather than placing all components and services into application-wide folders.

Key decisions:

**Standalone Components**

Standalone components eliminate the need for traditional NgModules and allow components to declare their dependencies directly.

**Angular Signals**

Signals provide a lightweight reactive approach to managing UI state.

They are used for state such as:
- Loading indicators.
- API errors.
- Weather information.
- Search queries and results.

**Services**

HTTP communication is separated from presentation components through injectable services. This keeps components focused on UI behaviour rather than API implementation details.

**RxJS**

RxJS handles asynchronous HTTP operations and provides operators for managing subscriptions and component lifecycles.

### Backend – Spring Boot

Spring Boot was selected because it provides an established ecosystem for developing REST APIs, dependency injection, database integration and external service communication.

The backend is organised by functional areas, including:
- Configuration.
- Exception handling.
- Task management.
- Weather integration.

This organisation keeps related functionality together and makes the application easier to maintain as it grows.

### Persistence – JPA, Hibernate and Spring Data JPA

The application uses Spring Data JPA with Hibernate and PostgreSQL.

These technologies serve different but complementary purposes.

**Jakarta Persistence (JPA)**

JPA is the persistence specification. It defines how Java objects can be mapped to relational database tables and provides a standard API for persistence operations.

**Hibernate**

Hibernate is the implementation of JPA used by the application.

It handles object-relational mapping, translates entity operations into SQL and maps database results back into Java objects.

**Spring Data JPA**

Spring Data JPA builds on top of JPA and simplifies database access through repository abstractions.

Instead of implementing repetitive database operations manually, repository interfaces provide common operations such as saving, retrieving and deleting entities.

This reduces boilerplate code and allows developers to focus on application-specific queries and business logic.

**PostgreSQL**

PostgreSQL was selected as the relational database because it provides reliable transactional support, data integrity and a mature relational database engine.

### Database Hosting – Neon

Neon provides hosted PostgreSQL, allowing the application to use a real relational database without depending on a locally running PostgreSQL server.

This became particularly useful during development when Docker Desktop presented configuration and connectivity issues.

Using Neon allowed development to continue without spending additional time troubleshooting the local database environment.

### Schema Management

The application uses Hibernate/JPA for entity-to-database mapping and schema management rather than introducing a separate migration framework during the initial development.

This keeps the initial implementation straightforward while the data model is evolving.

For a production environment, a versioned database migration strategy would be worth introducing to manage schema changes safely.

### Weather Integration

The backend acts as an intermediary between Angular and the external weather service.

The frontend communicates with the application's weather endpoints rather than directly accessing the external weather provider.

Spring's RestClient handles outbound HTTP communication.

This approach keeps external API integration details on the backend and allows the application to control how external weather data is retrieved and presented.

## Getting Started

### Prerequisites

Ensure the following are installed:

- Java Development Kit (JDK) 21.
- Apache Maven.
- Node.js and npm.
- A Neon PostgreSQL account or another accessible PostgreSQL instance.
- A modern web browser.

### 1. Clone the repository

```bash
git clone https://github.com/KabeloM06/kabelo-maswanganye-senior-assessment.git

cd kabelo-maswanganye-senior-assessment
```

### 2. Configure the database

Create a PostgreSQL database using Neon.

Configure the database connection in:

`server/taskmanager/src/main/resources/application-local.properties`

Use your own database credentials and connection details. Never commit database passwords or connection strings containing credentials.

The application uses the `local` Spring profile for local development configuration.

### 3. Run the backend

Navigate to the backend project:

```bash
cd server/taskmanager
```

Start Spring Boot with the local profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The backend will be available at:

```text
http://localhost:8080
```

### 4. Run the frontend

Open another terminal and navigate to the frontend:

```bash
cd client
```

Install dependencies:

```bash
npm install
```

Start the Angular development server:

```bash
npm start
```

The frontend will be available at:

```text
http://localhost:4200
```

## Running Tests

### Backend

From `server/taskmanager`:

```bash
mvn test
```

### Frontend

From `client`:

```bash
npm test
```

### Frontend production build

```bash
npm run build
```

## Challenges & Lessons Learned

Developing the application involved several practical challenges across the development environment, backend and frontend.

Rather than simply working around errors, these challenges provided opportunities to understand the underlying technologies and improve the implementation.

### 1. Database Setup and Docker Desktop

One of the initial challenges was setting up PostgreSQL locally using Docker.

Although Docker Compose was configured, Docker Desktop presented issues with its Linux container engine on Windows. We encountered problems connecting to the Docker engine, as well as difficulties starting containers from the project directory.

**Solution:**

We moved to Neon, a hosted PostgreSQL service.

This allowed the backend to connect to a real PostgreSQL database without requiring a locally running database container.

**Lesson learned:** Development infrastructure should support productivity rather than become a blocker. Using a managed database can be a practical alternative when local infrastructure introduces unnecessary complexity.

### 2. Spring Boot Database Configuration

During backend development, the application initially failed to start with errors including:

- `Failed to determine a suitable driver class`.
- `Failed to configure a DataSource: 'url' attribute is not specified`.
- `APPLICATION FAILED TO START`.

The issue was related to Spring Boot not loading the database configuration from the local profile.

The application had its database configuration in `application-local.properties`, but the local profile was not active when the application was started.

**Solution:**

We explicitly activated the local Spring profile when starting the application:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Lesson learned:** Spring Boot profiles are an important part of environment-specific configuration. Having the correct properties file is not sufficient if the corresponding profile is not activated.

### 3. Backend Repository and Persistence Issues

During implementation, we encountered compilation errors when application services attempted to call repository methods that had not yet been implemented.

For example, an incomplete repository definition resulted in errors involving methods such as `findAll()`.

**Solution:**

We corrected the repository implementation and ensured that the required Spring Data JPA repository abstractions were available to the application services.

**Lesson learned:** Understanding the relationship between entities, repositories and services is essential when building applications using Spring Data JPA.

### 4. Angular Service and Model Organisation

During weather integration, we encountered problems caused by inconsistent file organisation and duplicated service implementations.

There were initially two weather API services in different locations:

- `core/services/weather-api.service.ts`
- `features/weather/services/weather-api.service.ts`

This led to inconsistent method definitions and incorrect model imports.

Some of the errors included:

- `TS2305: Module has no exported member 'WeatherLocation'`.
- `TS2307: Cannot find module`.
- `TS2339: Property 'getWeather' does not exist on type 'WeatherApiService'`.

**Solution:**

We consolidated the weather functionality around the feature-specific service and corrected the model imports and method names.

We also ensured that the weather and weather-location interfaces were defined in their appropriate model files.

**Lesson learned:** Consistent feature organisation and clear ownership of services are important in Angular applications. Duplicated services can introduce unnecessary complexity and make integration more difficult.

### 5. Angular Signals and Template Integration

Another challenge occurred when integrating the weather search interface with the component logic.

The HTML template referenced properties and methods that had not yet been implemented in the TypeScript component.

Errors included:

- `Property 'searchQuery' does not exist on type 'WeatherPage'`.
- `Property 'searchResults' does not exist on type 'WeatherPage'`.
- `Property 'onSearchInput' does not exist on type 'WeatherPage'`.
- `Property 'selectLocation' does not exist on type 'WeatherPage'`.

**Solution:**

We aligned the component implementation with the template by adding the missing Signals and event-handling methods.

This included state for search queries, search results, loading indicators and search errors.

**Lesson learned:** Angular's template type checking helps catch inconsistencies between component logic and HTML at build time. Keeping component state and template bindings aligned is essential for maintainable applications.

### 6. Browser Geolocation

Implementing automatic weather retrieval introduced another consideration: browser geolocation is asynchronous and depends on user permission and browser support.

The application needed to handle cases where:
- The user grants location permission.
- The user denies location permission.
- The browser does not support geolocation.
- Retrieving the location takes too long.

**Solution:**

We implemented browser geolocation with a fallback to Pretoria.

The application retrieves weather information using the returned coordinates when permission is granted. Otherwise, it falls back to the default location.

**Lesson learned:** Browser APIs introduce environmental dependencies that should be handled gracefully. Applications should provide a sensible fallback rather than making optional browser capabilities mandatory.

## Future Improvements

Potential areas for further development include:

- More comprehensive frontend and backend test coverage.
- Improved task filtering and sorting.
- Additional weather information and forecast support.
- Improved API error handling and validation.
- Environment-specific configuration and secret management.
- Versioned database migrations.
- CI/CD automation.
- Production deployment.

## License

Developed as part of a technical assessment for a developer role at Glucode.