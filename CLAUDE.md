# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
./gradlew build              # Build, test, and package
./gradlew bootRun            # Run the Spring Boot app (requires Docker services)
./gradlew test               # Run all tests
./gradlew test --tests "shengov.bg.pizzza_management_app.ingredient.controller.IngredientControllerIT"  # Run single test class
./gradlew test --tests "*.IngredientControllerIT.create_ShouldReturnCreated_WhenIsAdmin"                # Run single test method
./gradlew spotlessCheck      # Check code formatting (Google Java Format)
./gradlew spotlessApply      # Auto-fix code formatting
```

Infrastructure (must be running for `bootRun`):
```bash
docker compose up -d         # Start PostgreSQL 16 + Keycloak 26.4.7
```

## Architecture

Spring Boot 4.0.1 REST API (Java 25) with OAuth2 Resource Server (Keycloak JWT), PostgreSQL, and feature-based package structure.

### Feature Module Structure

Each domain feature (`ingredient`, `size`, `pizza`) follows this layout:

```
{feature}/
  constant/        -> Validation message constants
  controller/      -> @RestController at /api/{feature}s
  dto/             -> Request/Response Java records
  exception/       -> Domain-specific exceptions + handler (@RestControllerAdvice @Order(1))
  mapper/          -> MapStruct interface (componentModel = SPRING)
  model/           -> JPA entity extending BaseEntity
  repository/      -> JpaRepository<Entity, UUID>
  service/         -> Interface + impls/ subdirectory with @Service implementation
```

Cross-cutting: `core/` (exception handling, BaseEntity), `security/` (SecurityConfig).

### Request Flow

Controller -> Service interface -> ServiceImpl -> Repository -> Entity

- **Controllers**: Accept `@Valid @RequestBody` requests, return `PagedModel<T>` for lists. POST returns 201 + Location header, PUT returns 200, DELETE returns 204.
- **Services**: Authorization enforced at service layer with `@PreAuthorize("hasRole('ADMIN')")` on write operations. Read operations available to any authenticated user. Each service validates name uniqueness via `existsByNameIgnoreCase()`.
- **DTOs**: All records. Requests use Jakarta Validation with message constants from `*ValidationMessages`. Responses use `@JsonInclude(NON_NULL)`.
- **Mappers**: MapStruct with `nullValuePropertyMappingStrategy = IGNORE`.
- **Exceptions**: Two-tier handling: domain-specific handlers (`@Order(1)`) for 409 conflicts, then `GlobalExceptionHandler` for cross-cutting errors. All errors return `ApiError` record format.

### Entity Model

- `BaseEntity` (@MappedSuperclass): UUID id, createdAt, updatedAt
- `PizzaEntity` has `@ManyToMany` ingredients and `@OneToMany` PizzaSize (composite key with price)
- Both pizza collections use `FetchType.LAZY` + `@BatchSize(20)`. `findWithDetailsById()` uses `@EntityGraph` for eager loading.

### Security

All endpoints require JWT authentication. Write operations require ADMIN role. Keycloak on port 8081, realm configured via `REALM_NAME` env var. Stateless sessions, CSRF disabled.

## Testing

Two test styles:
- **Integration tests** (`*IT`): Extend `BaseIntegrationTest` (Testcontainers PostgreSQL, MockMvc, `@Transactional` rollback). Use `@WithMockUser(roles = {"ADMIN"})` for auth. Tests organized with `@Nested` classes by operation.
- **Unit tests** (`*Test`): Mockito with `@ExtendWith(MockitoExtension.class)`.

Test method naming: `methodName_ShouldBehavior_WhenCondition`

Test utilities in `testutils/`: `MockMvcTestUtils` (HTTP helpers), `ObjectMapperTestUtils` (JSON serialization), `IngredientTestUtils` (test data factory).

## Code Conventions

- **Formatting**: Google Java Format enforced by Spotless — always run `./gradlew spotlessApply` before committing
- **Dependency injection**: Constructor injection via Lombok (`@RequiredArgsConstructor` with `private final` fields)
- **Naming**: Entities suffixed `Entity`, DTOs suffixed `Request`/`Response`, services suffixed `ServiceImpl` in `impls/` subpackage
- **IDs**: UUID throughout, auto-generated via `GenerationType.UUID`
- **Constants classes**: Private constructor throwing exception to prevent instantiation

## Git Commit Style

Subject line: `feat-{module}: description`, `feat: description`, `fix: description`, `chore: description`, `test-{module}: description`

Every commit must also include a body with the following sections (omit any section that is not applicable):

```
feat-{module}: short description

Why:
- Reason this change was needed, problem it solves, or motivation behind it

What:
- Summary of the new code, logic, or behaviour introduced
- Each meaningful change as a separate bullet

Notes (optional):
- Migration steps, config changes, caveats, follow-ups, or anything else useful
```

Example:

```
feat-ingredient: add Swagger UI with Keycloak OAuth2

Why:
- The API had no interactive documentation, making it hard to test endpoints manually

What:
- Added springdoc-openapi-starter-webmvc-ui 3.0.1 dependency
- Created OpenApiConfig defining OAuth2 Authorization Code + PKCE security scheme
- Configured Keycloak authorization/token URLs from existing issuer-uri property
- Permitted Swagger UI paths in SecurityConfig without authentication
- Added springdoc and oauth client-id configuration to application.yaml

Notes:
- Requires a public Keycloak client with Standard flow and the realm roles mapper
- Valid redirect URI must be set to http://localhost:8080/swagger-ui/oauth2-redirect.html
```