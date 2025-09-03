# 👥 Users Service - Microservicio de Gestión de Usuarios

[![Java 17](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

## 🎯 Descripción

El **Users Service** es el microservicio central responsable de la gestión de usuarios, roles y autenticación JWT distribuida en el sistema de Plazoleta de Comidas. Maneja todos los aspectos relacionados con usuarios, desde el registro hasta la autenticación y autorización.

## 🚀 Estado del Proyecto

✅ **COMPLETAMENTE FUNCIONAL**  
✅ **JWT Authentication** distribuido  
✅ **Arquitectura Hexagonal** implementada  
✅ **6 Endpoints** operativos  
✅ **PostgreSQL** configurado  
✅ **Validaciones** completas

## 🏗️ Arquitectura

### Patrón Hexagonal (Ports & Adapters)

```
📦 src/main/java/com/pragma/powerup/
├── 🎯 domain/
│   ├── model/          # Entidades de dominio
│   ├── api/            # Puertos (interfaces)
│   └── usecase/        # Casos de uso
├── 🔌 infrastructure/
│   ├── input/          # Adaptadores de entrada (REST)
│   ├── output/         # Adaptadores de salida (JPA)
│   ├── configuration/  # Configuración
│   └── security/       # Seguridad JWT
└── 🚀 application/     # DTOs, Handlers, Mappers, Utils
```

## 📊 Entidades del Dominio

### 👤 Usuario

```java
@Entity
public class Usuario {
    private Long id;
    private String firstName;     // Requerido
    private String lastName;      // Requerido
    private String document;      // Único, solo números
    private String phone;         // Formato internacional
    private LocalDate birthDate;  // Mayor de 18 años
    private String email;         // Único, formato válido
    private String password;      // Encriptado BCrypt
    private Role role;           // ADMIN, OWNER, EMPLOYEE, CUSTOMER
}
```

### 🛡️ Roles del Sistema

-   **👑 ADMIN**: Administrador del sistema (crear propietarios y restaurantes)
-   **🏪 OWNER**: Propietario de restaurante (gestionar menú y empleados)
-   **👷 EMPLOYEE**: Empleado (atender pedidos)
-   **👤 CUSTOMER**: Cliente (realizar pedidos)

## 🌐 API Endpoints

**Base URL**: `http://localhost:8081/api/v1`

### 🔐 Autenticación

#### POST `/auth/login`

**Descripción**: Autenticación de usuario y generación de JWT  
**Acceso**: 🌐 Público

```bash
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@plazoleta.com",
  "password": "customer123"
}
```

**Response**:

```json
{
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "user": {
        "id": 1,
        "firstName": "Admin",
        "lastName": "Sistema",
        "email": "admin@plazoleta.com",
        "role": "ADMIN"
    }
}
```

### 👥 Gestión de Usuarios

#### POST `/users/owner`

**Descripción**: Crear propietario de restaurante (HU-001)  
**Acceso**: 👑 Solo ADMIN

```bash
POST http://localhost:8081/api/v1/users/owner
Authorization: Bearer <ADMIN_JWT_TOKEN>
Content-Type: application/json

{
  "firstName": "Carlos",
  "lastName": "García",
  "document": "12345678",
  "phone": "+573001234567",
  "birthDate": "1985-05-15",
  "email": "carlos@restaurante.com",
  "password": "Password123!"
}
```

#### POST `/users/employee`

**Descripción**: Crear empleado para restaurante (HU-006)  
**Acceso**: 🏪 Solo OWNER

```bash
POST http://localhost:8081/api/v1/users/employee
Authorization: Bearer <OWNER_JWT_TOKEN>
Content-Type: application/json

{
  "firstName": "María",
  "lastName": "López",
  "document": "87654321",
  "phone": "+573009876543",
  "birthDate": "1990-08-20",
  "email": "maria@empleado.com",
  "password": "Employee123!",
  "restaurantId": 1
}
```

#### POST `/users/customer`

**Descripción**: Registro público de cliente (HU-008)  
**Acceso**: 🌐 Público

```bash
POST http://localhost:8081/api/v1/users/customer
Content-Type: application/json

{
  "firstName": "Ana",
  "lastName": "Martínez",
  "document": "11223344",
  "phone": "+573005551234",
  "birthDate": "1995-12-10",
  "email": "ana@cliente.com",
  "password": "Customer123!"
}
```

#### GET `/users/{id}`

**Descripción**: Obtener información pública de usuario  
**Acceso**: 🌐 Público

```bash
GET http://localhost:8081/api/v1/users/2
```

**Response**:

```json
{
    "id": 2,
    "firstName": "Owner",
    "lastName": "Restaurante",
    "email": "owner@plazoleta.com",
    "role": "OWNER"
}
```

#### GET `/users/{userId}/restaurant/{restaurantId}/is-employee`

**Descripción**: Verificar si un usuario es empleado de un restaurante específico  
**Acceso**: 🌐 Público (usado por otros microservicios)

```bash
GET http://localhost:8081/api/v1/users/8/restaurant/1/is-employee
```

**Response**:

```json
true
```

## ✅ Validaciones Implementadas

### 📧 Email

-   ✅ Formato válido requerido
-   ✅ Único en el sistema
-   ✅ No puede estar vacío

### 📱 Teléfono

-   ✅ Formato internacional (+57...)
-   ✅ Solo números después del código
-   ✅ Longitud válida

### 🆔 Documento

-   ✅ Solo números
-   ✅ Único en el sistema
-   ✅ Longitud mínima/máxima

### 🎂 Fecha de Nacimiento

-   ✅ Mayor de 18 años
-   ✅ Formato válido
-   ✅ No puede ser futura

### 🔒 Contraseña

-   ✅ Mínimo 8 caracteres
-   ✅ Al menos una mayúscula
-   ✅ Al menos un número
-   ✅ Encriptado con BCrypt

## 🔧 Configuración del Servicio

### Variables de Entorno (.env)

```properties
# Aplicación
SPRING_APPLICATION_NAME=users-service
PORT=8081

# Base de Datos PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/users_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_SCHEMA=public

# JWT Configuration (COMPARTIDO)
JWT_SECRET=change-me-change-me-change-me-change-me-change-me-change-me
JWT_EXPIRATION=86400000

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_PRAGMA=DEBUG
```

### Base de Datos

**PostgreSQL Database**: `users_db`

```sql
-- Tabla principal
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    document VARCHAR(20) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimización
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_document ON users(document);
CREATE INDEX idx_users_role ON users(role);
```

## 🧪 Testing

### Usuarios de Prueba

| Rol          | Email                     | Contraseña    | ID  |
| ------------ | ------------------------- | ------------- | --- |
| **ADMIN**    | `admin@plazoleta.com`     | `customer123` | 1   |
| **OWNER**    | `owner@plazoleta.com`     | `customer123` | 2   |
| **EMPLOYEE** | `employee@plazoleta.com`  | `customer123` | 8   |
| **CUSTOMER** | `customer1@plazoleta.com` | `customer123` | 3   |

### Ejecutar Tests

```bash
# Tests unitarios
./gradlew test

# Tests de integración
./gradlew integrationTest

# Cobertura de código
./gradlew jacocoTestReport
```

## 🚀 Ejecución del Servicio

### Desarrollo Local

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd Hexagonal-Users-Service

# 2. Configurar variables de entorno
cp .env.example .env
# Editar .env con los valores correctos

# 3. Iniciar PostgreSQL
docker run -d \
  --name postgres-users \
  -e POSTGRES_DB=users_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# 4. Ejecutar el servicio
./gradlew bootRun
```

### Verificación

```bash
# Health check
curl http://localhost:8081/actuator/health

# Swagger UI
http://localhost:8081/swagger-ui/index.html

# OpenAPI JSON
http://localhost:8081/v3/api-docs
```

## 📚 Documentación Adicional

### OpenAPI/Swagger

-   **Swagger UI**: http://localhost:8081/swagger-ui/index.html
-   **OpenAPI Spec**: http://localhost:8081/v3/api-docs

### Arquitectura

-   [Diagrama de Arquitectura](./docs/diagrams/04.Architecture.mmd)
-   [Historias de Usuario](./docs/HU/)

## 🔗 Integración con Otros Servicios

Este servicio es consultado por otros microservicios para:

-   **Restaurants Service**: Validar propietarios al crear restaurantes
-   **Orders Service**: Obtener información de usuarios para pedidos
-   **Messaging Service**: Obtener datos de contacto para notificaciones

### OpenFeign Clients

Otros servicios utilizan `UserServiceClient` para consultar este servicio:

```java
@FeignClient(name = "users-service", url = "${microservices.users.url}")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
```

## 🏆 Historias de Usuario Implementadas

-   ✅ **HU-001**: Crear Propietario (ADMIN)
-   ✅ **HU-005**: Autenticación JWT
-   ✅ **HU-006**: Crear Empleado (OWNER)
-   ✅ **HU-008**: Crear Cliente
