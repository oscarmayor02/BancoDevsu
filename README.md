# 🏦 Sistema Bancario - Arquitectura Microservicios

Prueba Técnica/Práctica - Microservicios con Spring Boot

## 📋 Descripción del Proyecto

Sistema bancario implementado con arquitectura de microservicios que gestiona:
- **Cliente-Persona**: Gestión de clientes y sus datos personales
- **Cuenta-Movimientos**: Gestión de cuentas bancarias y transacciones

### ✅ Funcionalidades Implementadas

- **F1**: Gestión completa de Clientes (CRUD)
- **F2**: Gestión completa de Cuentas (CRUD)
- **F3**: Registro de Movimientos (Depósitos/Retiros)
- **F4**: Reporte de movimientos por fechas y cliente (JSON)
- **F5**: Pruebas unitarias para dominio Cliente
- **F6**: Pruebas de integración implementadas
- **F7**: Desplegado en Docker con docker-compose

## 🏗️ Arquitectura

```
┌─────────────────┐      HTTP       ┌──────────────────┐
│  Cliente        │ ◄──────────────► │  Cuenta          │
│  Microservicio  │                  │  Microservicio   │
│  (Port 8081)    │                  │  (Port 8082)     │
└────────┬────────┘                  └────────┬─────────┘
         │                                    │
         │ JPA                                │ JPA
         ▼                                    ▼
┌─────────────────┐                  ┌──────────────────┐
│  PostgreSQL     │                  │  PostgreSQL      │
│  clientedb      │                  │  cuentadb        │
└─────────────────┘                  └──────────────────┘
         │                                    │
         └──────────────┬─────────────────────┘
                        │
                        ▼
                ┌───────────────┐
                │   RabbitMQ    │
                │ (Port 5672)   │
                └───────────────┘
```

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (Entity Framework Core equivalente)
- **PostgreSQL 15**
- **RabbitMQ** (Comunicación asíncrona)
- **Docker & Docker Compose**
- **Maven**
- **JUnit 5 & Mockito** (Pruebas unitarias)
- **Lombok**

## 📁 Estructura del Proyecto

```
proyecto-banco/
├── cliente-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/banco/cliente/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Persona.java
│   │   │   │   │   └── Cliente.java
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── cuenta-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/banco/cuenta/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Cuenta.java
│   │   │   │   │   └── Movimiento.java
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── config/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml
├── BaseDatos.sql
├── Banco-Microservicios.postman_collection.json
└── README.md
```

## 🚀 Instalación y Ejecución

### Prerrequisitos

- Docker Desktop instalado
- Java 17 (opcional, solo si ejecutas sin Docker)
- Maven 3.8+ (opcional)

### Paso 1: Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd proyecto-banco
```

### Paso 2: Levantar los Servicios con Docker

```bash
# Construir y levantar todos los contenedores
docker-compose up --build

# O en modo detached (segundo plano)
docker-compose up -d --build
```

### Paso 3: Verificar que los Servicios Están Corriendo

```bash
docker-compose ps
```

Deberías ver:
- `postgres-cliente` (Puerto 5432)
- `postgres-cuenta` (Puerto 5433)
- `rabbitmq` (Puerto 5672, Management: 15672)
- `cliente-service` (Puerto 8081)
- `cuenta-service` (Puerto 8082)

### Paso 4: Acceder a los Servicios

- **Cliente API**: http://localhost:8081/api/clientes
- **Cuenta API**: http://localhost:8082/api/cuentas
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 📊 Base de Datos

### Ejecutar Script SQL

```bash
# Para cliente database
docker exec -i postgres-cliente psql -U admin -d clientedb < BaseDatos.sql

# Para cuenta database
docker exec -i postgres-cuenta psql -U admin -d cuentadb < BaseDatos.sql
```

### Esquema de Base de Datos

**ClienteDB:**
- `persona` (PK: identificacion)
- `cliente` (hereda de persona, PK: identificacion, UK: cliente_id)

**CuentaDB:**
- `cuenta` (PK: numero_cuenta)
- `movimiento` (PK: id, FK: numero_cuenta)

## 🧪 Pruebas

### Ejecutar Pruebas Unitarias

```bash
# Cliente Service
cd cliente-service
mvn test

# Cuenta Service
cd cuenta-service
mvn test
```

### Pruebas con Postman

1. Importar `Banco-Microservicios.postman_collection.json` en Postman
2. Ejecutar las colecciones de prueba

## 📖 API Endpoints

### Cliente Service (Port 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/clientes` | Crear cliente |
| GET | `/api/clientes/{clienteId}` | Obtener cliente |
| GET | `/api/clientes` | Listar todos los clientes |
| PUT | `/api/clientes/{clienteId}` | Actualizar cliente |
| DELETE | `/api/clientes/{clienteId}` | Eliminar cliente |

### Cuenta Service (Port 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/cuentas` | Crear cuenta |
| GET | `/api/cuentas/{numeroCuenta}` | Obtener cuenta |
| GET | `/api/cuentas` | Listar todas las cuentas |
| GET | `/api/cuentas/cliente/{clienteId}` | Cuentas por cliente |
| PUT | `/api/cuentas/{numeroCuenta}` | Actualizar cuenta |
| DELETE | `/api/cuentas/{numeroCuenta}` | Eliminar cuenta |
| POST | `/api/movimientos` | Registrar movimiento |
| GET | `/api/movimientos/cuenta/{numeroCuenta}` | Movimientos de cuenta |
| GET | `/api/reportes?fechaInicio=...&fechaFin=...&cliente=...` | **Reporte F4** |

## 📝 Casos de Uso Implementados

### 1. Crear Usuarios (Clientes)
POST /api/clientes
```json

{
  "nombre": "Jose Lema",
  "genero": "Masculino",
  "edad": 30,
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785",
  "clienteId": "jose123",
  "contrasena": "1234",
  "estado": true
}
```

### 2. Crear Cuentas
POST /api/cuentas
```json

{
  "numeroCuenta": "478758",
  "tipo": "Ahorros",
  "saldoInicial": 2000,
  "estado": true,
  "clienteId": "jose123"
}
```

### 3. Registrar Movimientos


POST /api/movimientos
```json
{
  "numeroCuenta": "478758",
  "movimiento": -575
}
```

### 4. Generar Reporte (F4)

```
GET /api/reportes?fechaInicio=2025-02-01&fechaFin=2025-02-28&cliente=marianela456
```

**Respuesta JSON:**
```json
[
  {
    "fecha": "10/02/2025 10:00:00",
    "cliente": "Marianela Montalvo",
    "numeroCuenta": "225487",
    "tipo": "Corriente",
    "saldoInicial": 100,
    "estado": true,
    "movimiento": 600,
    "saldoDisponible": 700
  }
]
```

## 🔧 Configuración

### Variables de Entorno

Las configuraciones están en `application.yml` de cada servicio y pueden ser sobrescritas con variables de entorno en Docker:

```yaml
# Cliente Service
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-cliente:5432/clientedb
SPRING_RABBITMQ_HOST=rabbitmq

# Cuenta Service
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-cuenta:5432/cuentadb
CLIENTE_SERVICE_URL=http://cliente-service:8081
```

## 🐛 Troubleshooting

### Los contenedores no inician

```bash
# Ver logs
docker-compose logs -f

# Reiniciar servicios
docker-compose restart
```

### Error de conexión entre microservicios

Verificar que ambos servicios estén en la misma red:
```bash
docker network inspect proyecto-banco_banco-network
```

### Base de datos no se conecta

```bash
 # Verificar salud de PostgreSQL
docker exec postgres-cliente pg_isready -U admin
```

## 📦 Detener y Limpiar

```bash
  # Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Limpiar todo (cuidado: elimina datos)
docker-compose down -v --rmi all
```

## ✨ Características Adicionales Implementadas

- ✅ Validación de saldo insuficiente
- ✅ Comunicación asíncrona con RabbitMQ
- ✅ Manejo global de excepciones
- ✅ Logs estructurados
- ✅ Health checks en Docker
- ✅ Herencia JPA (Persona → Cliente)
- ✅ Transacciones con `@Transactional`
- ✅ Clean Architecture con DTOs
- ✅ Pruebas unitarias con Mockito

## 👤 Autor

Desarrollado por: [Tu Nombre]
Fecha: Noviembre 2024

## 📄 Licencia

Este proyecto es una prueba técnica educativa.