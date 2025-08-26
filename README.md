# usuarios-service (Spring Boot, Hexagonal)

-   Java 17 · Spring Boot 3 · Gradle 8
-   Hexagonal Architecture (domain, application, infrastructure)
-   OpenAPI 3.0 (springdoc)
-   Seguridad: JWT (vía Gateway) + autorización por roles con headers `X-User-*`
-   Testing: JUnit 5, WebMvc slice, ArchUnit, JaCoCo (≥80%)

## Funcionalidad cubierta (Historias de Usuario)

-   HU-001: Crear Propietario (ADMIN)
-   HU-005: Autenticación del sistema (login + JWT)
-   HU-006: Crear Empleado (OWNER)
-   HU-008: Autoregistro de Cliente (público)

## Endpoints principales

-   POST `/api/v1/auth/login` → Autenticación, retorna JWT
-   POST `/api/v1/users/owner` → Crear propietario (requiere rol ADMIN)
-   POST `/api/v1/users/employee` → Crear empleado (requiere rol OWNER)
-   POST `/api/v1/users/customer` → Crear cliente (público)

Documentación OpenAPI: ver `docs/openapi/usuarios.yaml`.

## Seguridad

-   El API Gateway valida el JWT y propaga `X-User-Id`, `X-User-Email`, `X-User-Role`.
-   Este servicio usa `HeaderAuthenticationFilter` y `@PreAuthorize`:
    -   `ADMIN` para `/users/owner`
    -   `OWNER` para `/users/employee`
    -   Público para `/auth/login` y `/users/customer`

## Ejecución local

```bash
./gradlew spotlessApply
./gradlew test jacocoTestReport
./gradlew bootRun
# Swagger UI: http://localhost:8081/swagger-ui/index.html
```

Variables relevantes (application.yml/env):

-   `spring.security.jwt.secret` (se usa para firmar tokens cuando este servicio genera JWT para login)
-   `spring.security.jwt.expiration` (ms)

## Arquitectura (resumen)

-   Domain: `UserModel`, `RoleEnum`, casos de uso `CreateOwnerUseCase`, `CreateEmployeeUseCase`, `CreateCustomerUseCase`, `AuthenticateUserUseCase`.
-   Ports IN: `IUserServicePort`, `IAuthServicePort`.
-   Ports OUT: `IUserPersistencePort`, `IPasswordEncoderPort`, `IDateProviderPort`, `IJwtProviderPort`.
-   Application: `UserHandler`, `AuthHandler`, mappers MapStruct, DTOs request/response.
-   Infrastructure:
    -   REST: `UserRestController`, `AuthRestController`
    -   JPA: `UserJpaAdapter`, `IUserRepository`, `UserEntity`, `IUserEntityMapper`
    -   Security: `SecurityConfiguration`, `HeaderAuthenticationFilter`, `JwtService`
    -   Configuración beans: `BeanConfiguration`

## Tests

-   Dominio: casos de uso (validaciones, reglas de negocio)
-   WebMvc: `AuthRestControllerWebMvcTest`, `UserRestControllerWebMvcTest`
-   Arquitectura: `HexagonalArchitectureTest`

## Convenciones y CI

-   Formato: Spotless (Google Java Format)
-   Cobertura: JaCoCo ≥ 80% (tarea `check`)
-   Ramas sugeridas: `feature/HU-xxx-descripcion` y tag al merge de cada HU

## Referencias

-   Diagrama HU: `docs/diagrams/HU/`
-   Requisitos: `docs/Requirements.md`
-   Guía ampliada: `docs/README.microservicios.md`
