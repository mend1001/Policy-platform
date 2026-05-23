# Plataforma de Gestión de Pólizas — API REST

**Candidato:** Miguel Angel Mendigaño Arismendy
**Prueba Técnica:** Desarrollador de Software Senior — Seguros Bolívar
**Módulo:** 2 — Prueba Técnica Práctica

---

## Descripción

API REST simplificada para gestión de pólizas de arrendamiento de inmuebles.
Soporta pólizas individuales y colectivas con sus respectivos riesgos,
renovación automática por IPC y cancelación.

---

## Requisitos

- Java 17
- Maven 3.8+

No requiere base de datos externa — usa H2 en memoria.

---

## Ejecución

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/polizas-platform.git
cd polizas-platform

# Ejecutar
./mvnw spring-boot:run
```

La aplicación levanta en `http://localhost:8080`

---

## Autenticación

Todos los endpoints requieren el siguiente header:

```
x-api-key: 123456
```

Sin este header la API responde `401 Unauthorized`.

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/polizas` | Listar pólizas por tipo y estado |
| GET | `/polizas/{id}/riesgos` | Listar riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Renovar póliza (ajusta canon por IPC) |
| POST | `/polizas/{id}/cancelar` | Cancelar póliza y sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agregar riesgo (solo pólizas colectivas) |
| POST | `/riesgos/{id}/cancelar` | Cancelar un riesgo individual |
| POST | `/core-mock/evento` | Mock de integración con CORE |

---

## Ejemplos de uso

### Listar pólizas colectivas activas
```bash
curl -X GET "http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA" \
  -H "x-api-key: 123456"
```

### Renovar una póliza
```bash
curl -X POST "http://localhost:8080/polizas/1/renovar" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"ipc": 0.09}'
```

### Agregar un riesgo
```bash
curl -X POST "http://localhost:8080/polizas/1/riesgos" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"aseguradoId": 2, "direccion": "Calle 123 # 45-67"}'
```

### Mock del CORE
```bash
curl -X POST "http://localhost:8080/core-mock/evento" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"evento": "ACTUALIZACION", "polizaId": 1}'
```

---

## Consola H2

Disponible en desarrollo en `http://localhost:8080/h2-console`

```
JDBC URL: jdbc:h2:mem:polizasdb
Usuario:  sa
Password: (vacío)
```

---

## Estructura del Proyecto

```
src/main/java/com/segurosbolivar/polizas/
  ├── controller/    → endpoints REST
  ├── service/       → lógica de negocio
  ├── repository/    → acceso a datos JPA
  ├── model/         → entidades (Policy, Risk)
  ├── dto/           → objetos request/response
  ├── exception/     → manejo de errores
  └── security/      → validación x-api-key
```

> La arquitectura en capas refleja los principios de la arquitectura hexagonal
> descrita en el Módulo 1: controller = adaptador de entrada,
> service = dominio, repository = adaptador de salida.

---

## Documentación Técnica

La documentación de arquitectura, modelo de datos y decisiones técnicas
se encuentra en `docs/obsidian/` — compatible con Obsidian.
"# Policy-platform" 
