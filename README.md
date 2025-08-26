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

## Integraciones con otros microservicios

-   Dependencias síncronas (salientes)

    -   No aplica por defecto. Este servicio no invoca a otros microservicios.

-   Endpoints expuestos para otros servicios

    -   `GET /api/v1/usuarios/{id}` → obtener usuario por id (orders, restaurants, messaging, traceability)
    -   `GET /api/v1/usuarios/{id}/activo` → validar si el usuario está activo
    -   Opcionales (según necesidades de negocio):
        -   `GET /api/v1/usuarios/validar-empleado/{empleadoId}/restaurante/{restauranteId}`
        -   `GET /api/v1/usuarios/validar-propietario/{propietarioId}/restaurante/{restauranteId}`
    -   Contrato: ver `docs/openapi/usuarios.yaml`.

-   Consumo (cómo otros servicios deben configurarse)

    -   Dependencia: `org.springframework.cloud:spring-cloud-starter-openfeign`
    -   `application.yml` (timeouts/URL):
        ```yaml
        feign:
            client:
                config:
                    default:
                        connectTimeout: 3000
                        readTimeout: 5000
                        loggerLevel: basic
        microservices:
            users:
                url: ${MICROSERVICES_USERS_URL:http://localhost:8081}
        ```
    -   Variable de entorno sugerida: `MICROSERVICES_USERS_URL=http://localhost:8081`
    -   Interfaz Feign de ejemplo (en servicios consumidores):
        ```java
        @FeignClient(name = "users-service", url = "${microservices.users.url}")
        interface UsersServiceClient {
          @GetMapping("/api/v1/usuarios/{id}")
          UsuarioResponseDto getUser(@PathVariable Long id);
          @GetMapping("/api/v1/usuarios/{id}/activo")
          Boolean isActive(@PathVariable Long id);
        }
        ```

-   Seguridad y Gateway

    -   Externo: el Gateway valida JWT y propaga `X-User-*`.
    -   Servicio-a-servicio: usar un `RequestInterceptor` Feign para inyectar `Authorization: Bearer <token>` o, si se permite, headers `X-User-*` de un usuario técnico.

-   Asíncrono (mensajería)

    -   No aplica (este servicio no publica ni consume eventos en esta versión).

-   Opcional (Service Discovery/Config Server)

    -   Service Discovery (Eureka): usar `lb://users-service` en lugar de URL fija.
    -   Config Server: externalizar propiedades y rutas Feign.

-   Checklist para producción
    -   [ ] Endpoints de validación expuestos y documentados (OpenAPI actualizado)
    -   [ ] Consumidores con timeouts/retries y logs/metricas de integraciones
    -   [ ] Seguridad servicio-a-servicio definida (token técnico o mTLS) según política

## Convenciones y CI

-   Formato: Spotless (Google Java Format)
-   Cobertura: JaCoCo ≥ 80% (tarea `check`)
-   Ramas sugeridas: `feature/HU-xxx-descripcion` y tag al merge de cada HU

## Referencias

-   Diagrama HU: `docs/diagrams/HU/`
-   Requisitos: `docs/Requirements.md`
-   Guía ampliada: `docs/README.microservicios.md`
