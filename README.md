# Polizas Platform — API REST para gestión de pólizas de arrendamiento

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![H2](https://img.shields.io/badge/DB-H2%20in--memory-blue)
![Tests](https://img.shields.io/badge/Tests-29%20passing-success)

API REST que gestiona el ciclo de vida de pólizas de arrendamiento: creación de riesgos, renovación por IPC y cancelación en cascada.

---

## Descripción

El sistema administra dos tipos de pólizas (`INDIVIDUAL` y `COLECTIVA`) con sus riesgos asociados. Aplica reglas de negocio específicas: una póliza individual solo puede tener un riesgo activo, los riesgos solo se agregan a pólizas colectivas, y la cancelación de una póliza cancela todos sus riesgos automáticamente.

La renovación aplica el Índice de Precios al Consumidor (IPC) al canon y recalcula la prima proporcionalmente al periodo de vigencia. Toda operación de renovación o cancelación notifica al sistema CORE mediante un mock de integración.

---

## Prerrequisitos

- Java 17
- Maven 3.8+ (o usar el wrapper incluido `./mvnw`)

No requiere base de datos externa — usa H2 en memoria.

---

## Quick Start

```bash
# 1. Clonar
git clone https://github.com/tu-usuario/polizas-platform.git
cd polizas-platform

# 2. Ejecutar
./mvnw spring-boot:run

# 3. Verificar
curl -H "x-api-key: 123456" http://localhost:8080/polizas
```

La API levanta en `http://localhost:8080`. Los datos de prueba se cargan automáticamente desde `data.sql`.

---

## Autenticación

Todos los endpoints requieren el header:

```
x-api-key: 123456
```

Sin este header la API responde `401 Unauthorized` con cuerpo `{ "error": "API Key inválida o ausente" }`.

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/polizas` | Listar pólizas (filtros opcionales: `tipo`, `estado`) |
| `GET` | `/polizas/{id}/riesgos` | Listar riesgos de una póliza |
| `POST` | `/polizas/{id}/renovar` | Renovar póliza con ajuste de IPC |
| `POST` | `/polizas/{id}/cancelar` | Cancelar póliza y todos sus riesgos activos |
| `POST` | `/polizas/{id}/riesgos` | Agregar riesgo (solo pólizas `COLECTIVA`) |
| `POST` | `/riesgos/{id}/cancelar` | Cancelar un riesgo individual |
| `POST` | `/core-mock/evento` | Mock de integración con sistema CORE |

**Valores de enum:**
- `tipo`: `INDIVIDUAL` | `COLECTIVA`
- `estado`: `ACTIVA` | `RENOVADA` | `CANCELADA`

---

## Ejemplos de uso

### Listar pólizas colectivas activas
```bash
curl -X GET "http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA" \
  -H "x-api-key: 123456"
```

### Renovar póliza con IPC del 9%
```bash
curl -X POST "http://localhost:8080/polizas/1/renovar" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"ipc": 0.09}'
```
Respuesta: póliza actualizada con `canon * 1.09`, prima recalculada, estado `RENOVADA`.

### Cancelar póliza
```bash
curl -X POST "http://localhost:8080/polizas/3/cancelar" \
  -H "x-api-key: 123456"
```
Respuesta: póliza en estado `CANCELADA`. Todos sus riesgos activos pasan a `CANCELADO`.

### Agregar riesgo a póliza colectiva
```bash
curl -X POST "http://localhost:8080/polizas/3/riesgos" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"aseguradoId": 10, "direccion": "Calle 123 # 45-67"}'
```

### Cancelar riesgo individual
```bash
curl -X POST "http://localhost:8080/riesgos/1/cancelar" \
  -H "x-api-key: 123456"
```

### Enviar evento al CORE
```bash
curl -X POST "http://localhost:8080/core-mock/evento" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"evento": "ACTUALIZACION", "polizaId": 1}'
```

---

## Estructura del proyecto

```
src/main/java/com/segurosbolivar/polizas/
├── PolizasApplication.java          Punto de entrada Spring Boot
├── config/
│   └── AppProperties.java           @ConfigurationProperties — config centralizada
├── model/
│   ├── Policy.java                  Entidad póliza (@OneToMany → Risk)
│   ├── Risk.java                    Entidad riesgo (@ManyToOne → Policy)
│   └── enums/                       PolicyType, PolicyState, RiskState
├── repository/
│   ├── PolicyRepository.java        Queries por tipo, estado, combinado
│   └── RiskRepository.java          findByPolizaId
├── dto/
│   ├── request/                     RenovarPolicyRequest, AgregarRiskRequest, CoreEventRequest
│   └── response/                    PolicyResponse, RiskResponse, ErrorResponse (con from())
├── exception/
│   ├── BusinessException.java       RuntimeException + HttpStatus → 400
│   ├── ResourceNotFoundException.java                              → 404
│   └── GlobalExceptionHandler.java  @RestControllerAdvice — respuesta uniforme
├── security/
│   └── ApiKeyFilter.java            OncePerRequestFilter — valida x-api-key
├── service/
│   ├── PolicyService.java           Interfaz de contrato
│   ├── RiskService.java
│   ├── CoreMockService.java
│   ├── validation/                  Strategy pattern para validaciones de negocio
│   └── impl/                        PolicyServiceImpl, RiskServiceImpl, CoreMockServiceImpl
└── controller/
    ├── PolicyController.java        5 endpoints en /polizas
    ├── RiskController.java          POST /riesgos/{id}/cancelar
    └── CoreMockController.java      POST /core-mock/evento

src/main/resources/
├── application.yaml                 Configuración H2, JPA, propiedades de app
└── data.sql                         4 pólizas + 7 riesgos de prueba
```

---

## Patrones de diseño aplicados

| Patrón | Ubicación | Propósito |
|--------|-----------|-----------|
| Strategy | `service/validation/` | Validaciones de negocio intercambiables por `@Qualifier` |
| Layered Architecture | Controller → Service → Repository | Separación de responsabilidades |
| DTO | `dto/request/`, `dto/response/` | Nunca exponer entidades JPA |
| Factory (static) | `PolicyResponse.from()`, `RiskResponse.from()` | Conversión entidad → DTO |
| Filter Chain | `ApiKeyFilter` | Cross-cutting concern de seguridad |

---

## Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Con reporte de cobertura
./mvnw verify
```

**29 casos de prueba** distribuidos en:
- `PolicyServiceImplTest` — renovación, cancelación, listado con filtros, reglas de negocio
- `RiskServiceImplTest` — agregar riesgo, validación tipo COLECTIVA, cancelar riesgo
- `PolicyControllerTest` — integración MockMvc con todos los endpoints de pólizas
- `RiskControllerTest` — integración MockMvc con cancelación de riesgo

---

## Decisiones técnicas

**H2 en memoria:** permite ejecución con un solo comando sin dependencias externas. Los datos de prueba en `data.sql` reproducen escenarios reales (pólizas INDIVIDUAL/COLECTIVA, riesgos activos/cancelados).

**Arquitectura en capas estricta:** cada capa tiene una sola responsabilidad. Los controllers no contienen lógica de negocio. Los services no conocen HTTP. Los repositories no conocen DTOs.

**Strategy pattern para validaciones:** `PolicyValidationStrategy` permite agregar nuevas validaciones sin modificar los servicios existentes. Cada validación es un `@Component` inyectable por `@Qualifier`.

**`BusinessException` con `HttpStatus`:** una sola excepción de negocio carga su propio código de respuesta. El `GlobalExceptionHandler` la captura y construye la respuesta sin lógica adicional.

**DTOs con `from()` estático:** la conversión entidad → respuesta vive en el DTO mismo. Sin mappers externos, sin dependencias adicionales.

---

## Consola H2

Disponible en desarrollo en `http://localhost:8080/h2-console`

```
JDBC URL: jdbc:h2:mem:polizasdb
Usuario:  sa
Password: (vacío)
```

---

*Prueba técnica Módulo 2 — Seguros Bolívar*
