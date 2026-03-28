# ExploreBangladesh

ExploreBangladesh is a Spring Boot travel platform with static frontend pages and REST APIs for hotels, flights, cars, guides, nearby places, travel packages, and itinerary planning.

## Current Project Status

The project currently includes:

- JWT-based authentication and refresh token flow
- Google OAuth2 login integration
- Hotels search module
- Flights search module
- Cars search module
- Guides search module
- Nearby places recommendation module
- Travel plans module with itinerary and cost breakdown
- Static frontend pages served from Spring Boot

## Tech Stack

- Java 21
- Spring Boot 3.4.3
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL (primary) and H2 (runtime option)
- JWT (jjwt 0.12.6)
- OAuth2 Client (Google)
- ModelMapper
- Lombok
- Apache HttpClient5
- OpenAPI annotations (Swagger)
- Maven Wrapper

## Repository Structure

```text
ExploreBangladesh/
|-- src/
|   |-- main/
|   |   |-- java/com/TeamDeadlock/ExploreBangladesh/
|   |   |   |-- auth/
|   |   |   |   |-- config/
|   |   |   |   |-- controller/
|   |   |   |   |-- dto/
|   |   |   |   |-- entity/
|   |   |   |   |-- exception/
|   |   |   |   |-- payload/
|   |   |   |   |-- repository/
|   |   |   |   `-- service/
|   |   |   |-- config/
|   |   |   |-- controller/
|   |   |   |-- dto/
|   |   |   |-- entity/
|   |   |   |-- repository/
|   |   |   `-- service/
|   |   `-- resources/
|   |       |-- application.properties
|   |       |-- application.properties.example
|   |       `-- static/
|   |           |-- index.html
|   |           |-- hotels.html
|   |           |-- flights.html
|   |           |-- cars.html
|   |           |-- guides.html
|   |           |-- places.html
|   |           |-- travel-plans.html
|   |           |-- itinerary.html
|   |           |-- login.html
|   |           |-- signup.html
|   |           |-- profile.html
|   |           |-- styles.css
|   |           `-- js/
|   `-- test/
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- run.cmd
|-- run.ps1
|-- CHANGELOG.md
`-- README.md
```

## Prerequisites

- JDK 21
- MySQL 8+ (or use H2)
- Internet access for external APIs
- API credentials for:
  - Geoapify
  - Amadeus
  - Google OAuth2 (optional but recommended)

## Configuration

1. Copy [src/main/resources/application.properties.example](src/main/resources/application.properties.example) to [src/main/resources/application.properties](src/main/resources/application.properties).
2. Set values for:
   - Database URL, username, password
   - `geoapify.api.key`
   - `amadeus.api.key`
   - `amadeus.api.secret`
   - Security and OAuth2 values as required

Important: [src/main/resources/application.properties](src/main/resources/application.properties) is intentionally ignored by git.

## Running the Application

### Windows (recommended)

Use one of the helper scripts in project root:

```bat
run.cmd
```

or

```powershell
.\run.ps1
```

### Maven wrapper

```bat
mvnw.cmd spring-boot:run -Prun
```

Application URL:

- http://localhost:8080

## Build and Test

```bat
mvnw.cmd clean test
mvnw.cmd clean package
```

## API Summary

### Authentication and Users

- `/api/v1/auth`
  - `POST /login`
  - `POST /refresh`
  - `POST /logout`
  - `POST /register`
  - `POST /signup`
- `/api/v1/profile`
  - `GET /me`
  - `PUT /me`
- `/api/v1/users`
  - User management CRUD endpoints

### Travel Modules

- `/api/hotels`
  - `POST /search`
  - `GET /test`
- `/api/flights`
  - `POST /search`
  - `GET /airports`
  - `GET /test`
- `/api/cars`
  - `GET /`
  - `GET /cities`
  - `GET /test`
- `/api/guides`
  - `GET /`
  - `GET /cities`
  - `GET /test`
- `/api/places`
  - `GET /nearby`
  - `GET /locations`
  - `GET /popular`
  - `GET /categories`
  - `GET /by-category`
  - `GET /search`
  - `GET /test`
- `/api/plans`
  - `GET /`
  - `GET /{id}`
  - `GET /test`

## Backend Architecture

The backend follows a layered Spring Boot architecture:

- Controller layer: handles HTTP requests/responses and route contracts.
- Service layer: implements business workflows, integration orchestration, and response shaping.
- Repository layer: Spring Data JPA repositories for persistence and query abstraction.
- Entity/DTO layer: JPA entities represent stored data; DTOs/payloads shape API contracts.
- Config/Initializer layer: security chain, CORS, OpenAPI settings, and startup seed logic.

Primary package areas:

- [src/main/java/com/TeamDeadlock/ExploreBangladesh/controller](src/main/java/com/TeamDeadlock/ExploreBangladesh/controller)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/service](src/main/java/com/TeamDeadlock/ExploreBangladesh/service)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/repository](src/main/java/com/TeamDeadlock/ExploreBangladesh/repository)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/entity](src/main/java/com/TeamDeadlock/ExploreBangladesh/entity)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth)

## Backend Request Flow

Typical request lifecycle:

1. Request enters Spring Security filter chain.
2. JWT filter attempts token extraction from `Authorization: Bearer` header or auth cookie.
3. Security context is established for authenticated requests.
4. Controller validates/accepts request and delegates to service.
5. Service executes business logic and calls repositories/external clients.
6. DTO/payload is returned as a structured HTTP response.

Representative references:

- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/JwtAuthenticationFilter.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/JwtAuthenticationFilter.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/SecurityConfig.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/SecurityConfig.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/controller/AuthController.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/controller/AuthController.java)

## Security and Authentication

The project uses stateless security with Spring Security + JWT and supports Google OAuth2 sign-in.

- Access token: short-lived JWT used for API authorization.
- Refresh token: rotation-based flow for session continuation.
- Refresh token persistence: refresh tokens are tracked in storage for revocation/rotation.
- Logout: revokes persisted refresh token and clears refresh cookie.
- OAuth2 success: user is provisioned/resolved, then token issuance + frontend redirect occurs.

Key implementation files:

- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/SecurityConfig.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/SecurityConfig.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/service/impl/JwtService.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/service/impl/JwtService.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/OAuth2SuccessHandler.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/OAuth2SuccessHandler.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/repository/RefreshTokenRepository.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/repository/RefreshTokenRepository.java)

Authorization boundaries are configured from constants and enforced in the security chain:

- Public routes: auth and selected travel APIs.
- Admin routes: user-management endpoints.

## Data and Persistence

Persistence uses Spring Data JPA (Hibernate) with MySQL as primary storage and H2 as an optional runtime/testing path.

- Repositories use derived query methods and selective JPQL.
- Domain entities cover travel plans, places, guides, cars, flights metadata, and auth domain data.
- Travel plans are modeled as an aggregate (plan -> day -> activities + cost breakdown).

Representative data model files:

- [src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/TravelPlanEntity.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/TravelPlanEntity.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/ItineraryDayEntity.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/ItineraryDayEntity.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/ItineraryActivityEntity.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/ItineraryActivityEntity.java)
- [src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/CostBreakdownEntity.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/entity/CostBreakdownEntity.java)

## Data Initialization Behavior

On startup, seeders populate baseline datasets only when target tables are empty.

- General domain data seeding (coordinates, airports, airlines, cars, guides, places):
  [src/main/java/com/TeamDeadlock/ExploreBangladesh/config/DataInitializer.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/config/DataInitializer.java)
- Travel plan seeding (budget/standard/premium plans with itinerary details):
  [src/main/java/com/TeamDeadlock/ExploreBangladesh/config/TravelPlanDataInitializer.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/config/TravelPlanDataInitializer.java)
- Role seeding for auth domain:
  [src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/RoleInitializer.java](src/main/java/com/TeamDeadlock/ExploreBangladesh/auth/config/RoleInitializer.java)

This behavior is useful for local onboarding but should be reviewed for production rollout policies.

## External Integrations

Current backend integrations include:

- Geoapify: hotel/places geospatial enrichment.
- Amadeus: flight search and related metadata.
- Google OAuth2: federated login flow.

Credentials and related runtime values are configured through:

- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/resources/application.properties.example](src/main/resources/application.properties.example)

## Operational Notes

- Preferred local run scripts (Windows):
  - [run.cmd](run.cmd)
  - [run.ps1](run.ps1)
- Maven wrapper profile for run operations:
  - `mvnw.cmd spring-boot:run -Prun`
- Build definition and dependency source of truth:
  - [pom.xml](pom.xml)

For secure team workflows, keep secrets out of version control and use the example properties file as the baseline template.

## Frontend Pages

Pages are served from [src/main/resources/static](src/main/resources/static):

- [src/main/resources/static/index.html](src/main/resources/static/index.html)
- [src/main/resources/static/hotels.html](src/main/resources/static/hotels.html)
- [src/main/resources/static/flights.html](src/main/resources/static/flights.html)
- [src/main/resources/static/cars.html](src/main/resources/static/cars.html)
- [src/main/resources/static/guides.html](src/main/resources/static/guides.html)
- [src/main/resources/static/places.html](src/main/resources/static/places.html)
- [src/main/resources/static/travel-plans.html](src/main/resources/static/travel-plans.html) (Packages)
- [src/main/resources/static/itinerary.html](src/main/resources/static/itinerary.html)
- [src/main/resources/static/login.html](src/main/resources/static/login.html)
- [src/main/resources/static/signup.html](src/main/resources/static/signup.html)
- [src/main/resources/static/profile.html](src/main/resources/static/profile.html)


