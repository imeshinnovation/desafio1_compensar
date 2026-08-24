# Employees API

API REST para la gestión de **empleados** y sus **jornadas laborales** (work shifts).
Prueba técnica construida con Spring Boot, aplicando Clean Architecture y Domain-Driven Design con criterio, manteniendo el proyecto pequeño y acotado.

## Requisitos

- **Java 21+** (el proyecto compila con `--release 21`; verificado en JDK 25 y 26)
- **Maven 3.9+**

## Cómo ejecutar

Con Docker (recomendado — incluye PostgreSQL):

```bash
cp .env.example .env   # credenciales de la base (una vez; .env no se commitea)
docker compose up -d --build
```

La aplicación queda en `http://localhost:8080` y PostgreSQL en `localhost:5432`. La base se
inicializa en el primer arranque con `init.sql` (esquema + 30 empleados + 30 jornadas en los
últimos 90 días) y los datos **persisten** en el volumen `postgres-data` entre reinicios.

Sin Docker (requiere un PostgreSQL local, p. ej. `docker compose up -d db`):

```bash
mvn spring-boot:run
```

## Documentación y herramientas

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Acceso a la base de datos

```bash
docker exec -it employees-db psql -U employees -d employees
```

Credenciales: usuario `employees`, contraseña `employees`, base `employees` — se leen de
`.env` (copiar desde `.env.example`), que está en `.gitignore`; el compose falla si faltan
(no hay valores por defecto en el repositorio).

**Persistencia**: los datos viven en el volumen Docker `postgres-data` y sobreviven a
`docker compose down`/`up`. El `init.sql` solo se ejecuta en el primer arranque del volumen
(esquema + seed); para reiniciar desde cero: `docker compose down -v && docker compose up -d`.

## Endpoints

Prefijo base: `/api/v1`

### Empleados (CRUD)

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| `POST` | `/api/v1/employees` | Crear empleado | `201 Created` · `400` validación · `409` documento/email duplicado |
| `GET` | `/api/v1/employees` | Listar empleados (ordenados por apellido) | `200` |
| `GET` | `/api/v1/employees/{id}` | Obtener empleado por id | `200` · `404` |
| `PUT` | `/api/v1/employees/{id}` | Reemplazar perfil completo (semántica de PUT) | `200` · `400` · `404` · `409` |
| `DELETE` | `/api/v1/employees/{id}` | Eliminar empleado (jornadas en cascada) | `204` · `404` |

### Jornadas laborales

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/employees/{id}/work-shifts` | Listar jornadas del empleado; filtro opcional por rango con `from` y `to` (`yyyy-MM-dd`, inclusive) |

Ejemplo:

```bash
curl "http://localhost:8080/api/v1/employees/1/work-shifts?from=2026-08-19&to=2026-08-21"
```

### Ejemplo de creación

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ana",
    "lastName": "Gómez",
    "documentId": "CC-1001",
    "email": "ana.gomez@example.com",
    "position": "Software Engineer",
    "hireDate": "2023-03-15"
  }'
```

El campo `status` es opcional al crear (por defecto `ACTIVE`); es requerido en el `PUT`.

### Formato de error

Todas las respuestas de error usan un cuerpo consistente, manejado de forma centralizada:

```json
{
  "timestamp": "2026-08-21T22:44:39.835902Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 9999 not found",
  "path": "/api/v1/employees/9999"
}
```

## Arquitectura

Clean Architecture con dependencias siempre hacia el dominio; el dominio no conoce Spring, JPA ni HTTP:

```
com.compensar.employees
├── domain           # Entidades y value objects puros, puertos de repositorio, excepciones de dominio
├── application      # Casos de uso (un caso de uso = una responsabilidad) y comandos de entrada
├── infrastructure
│   ├── persistence  # Entidades JPA (separadas del dominio), adaptadores de puertos, mappers
│   └── config       # Bean de OpenAPI y seed de datos al arrancar
└── presentation     # Controladores REST, DTOs de request/response, mappers y manejo de errores
```

### Decisiones de diseño

- **Regla de dependencia**: los casos de uso dependen de interfaces (`domain.repository`), nunca de implementaciones; los controladores exponen únicamente DTOs; los adaptadores de persistencia aíslan JPA del dominio.
- **Puertos segregados (ISP/CQS)**: los repositorios del dominio se dividen en puertos de consulta (`EmployeeQueryRepository`, `WorkShiftQueryRepository`) y de comando (`*CommandRepository`), de modo que cada caso de uso depende solo de los métodos que utiliza. El endpoint de jornadas vive en su propio `WorkShiftController`.
- **Entidades JPA vs. dominio**: `EmployeeEntity`/`WorkShiftEntity` viven en infraestructura; el modelo de dominio (`Employee`, `WorkShift`) son POJOs puros con comportamiento propio (fábrica validada, `hoursWorked()` derivado). Evita un modelo anémico sin forzar patrones tácticos de DDD que serían sobre-ingeniería en este alcance.
- **`hoursWorked` calculado, no almacenado**: se deriva de `startTime`/`endTime`, eliminando datos redundantes e inconsistencias.
- **Errores centralizados**: `@RestControllerAdvice` traduce excepciones de dominio a HTTP con significados claros: `404` (no encontrado), `409` (unicidad violada), `400` (validación Bean Validation, rango de fechas inválido, tipos incorrectos).
- **Unicidad**: `documentId` y `email` se validan en el caso de uso (respuesta `409` explícita) y además tienen constraint único en base de datos como red de seguridad.
- **PostgreSQL con seed vía `init.sql`**: la base corre en el stack Docker con volumen persistente; el esquema y los datos de ejemplo se cargan una única vez (`CURRENT_DATE - INTERVAL` mantiene el demo reciente) y Hibernate arranca con `ddl-auto: validate` para que el esquema no se desvíe del mapeo JPA. Java queda libre de seeding.
- **IDs `Long` autoincrementales**: suficiente para este alcance; la opción `UUID` del enunciado añadiría complejidad sin beneficio aquí.
- **`from`/`to` opcionales e inclusivos**: el adaptador de repositorio elige la consulta derivada de Spring Data adecuada según los extremos presentes.
- **JDK 23+ y Lombok**: el pom configura `maven-compiler-plugin` con `<proc>full</proc>` y `annotationProcessorPaths`, porque JDK 23+ deshabilita el procesamiento de anotaciones implícito.

## Seguridad (fuera de alcance)

La API **no implementa autenticación ni autorización**: cualquier cliente puede consultar o
modificar cualquier empleado. Es una decisión de alcance de la prueba técnica (el enunciado no
incluye seguridad y el foco es el diseño de la arquitectura); los datos son de ejemplo y los
puertos publicados se limitan a `127.0.0.1`. En un despliegue real se añadiría Spring Security
(autenticación + autorización por rol, p. ej. `@PreAuthorize` en los controladores) como capa
de presentación, sin tocar el dominio.

## Pruebas

- **Unitarias** (JUnit 5 + Mockito): los casos de uso `CreateEmployee`, `UpdateEmployee`, `DeleteEmployee` y `ListWorkShifts`, mockeando los puertos de repositorio.
- **De integración** (`@SpringBootTest` + MockMvc): corren contra un PostgreSQL real inicializado con el mismo `init.sql` del despliegue (una sola fuente de verdad para el seed); cada test se ejecuta en una transacción que se revierte al final, dejando los datos intactos. Hay **dos modos de ejecución**:

#### Modo 1 — Testcontainers (por defecto)

Levanta un PostgreSQL efímero automáticamente. Requiere Docker en ejecución:

```bash
# Docker Desktop
mvn test

# Colima (MacOS)
export DOCKER_HOST=unix://$HOME/.colima/default/docker.sock
export TESTCONTAINERS_RYUK_DISABLED=true
mvn test
```

#### Modo 2 — Base local existente (sin contenedor extra)

Útil para correr contra el PostgreSQL del propio stack Docker (`localhost:5432`) o cualquier base ya levantada. No crea ningún contenedor:

```bash
set -a && source .env && set +a   # carga POSTGRES_USER/PASSWORD/DB
TEST_EXTERNAL_DB_URL="jdbc:postgresql://localhost:5432/${POSTGRES_DB}" \
TEST_EXTERNAL_DB_USERNAME="${POSTGRES_USER}" \
TEST_EXTERNAL_DB_PASSWORD="${POSTGRES_PASSWORD}" \
mvn test
```

La base apuntada no se modifica: los cambios de cada test se revierten con una transacción.

## Stack

Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL · Testcontainers · Lombok · springdoc-openapi · JUnit 5 + Mockito · Bean Validation

### Autor

## Alexander Rubio Cáceres
Ingeniero de Software
Especialista en Seguridad de la Información
Desarrollador FullStack
