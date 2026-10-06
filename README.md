# Exp3_S8 – Desarrollo Backend III

## 1. Objetivo

Implementar una arquitectura backend basada en microservicios con Spring Cloud, incorporando seguridad OAuth 2.0/JWT, tolerancia a fallos con Resilience4j, descubrimiento de servicios con Eureka, configuración centralizada con Config Server, mensajería asíncrona con Apache Kafka y contenedorización mediante Docker y Docker Compose.

La solución continúa el trabajo realizado en las semanas anteriores sobre el backend bancario basado en los datos migrados del sistema legacy.

## 2. Arquitectura

Componentes principales:

- **config-server** – servidor centralizado de configuración, puerto `8888`.
- **discovery-server** – Eureka Service Discovery, puerto `8761`.
- **authorization-server** – servidor OAuth 2.0, puerto `9000`.
- **bank-service** – microservicio bancario y Resource Server OAuth 2.0, puerto `8091`.
- **Oracle XE** – persistencia de los datos bancarios.
- **Apache Kafka** – mensajería asíncrona mediante el topic `bank-transacciones`.

Flujo de seguridad:

1. El cliente solicita un Access Token al Authorization Server.
2. El Authorization Server entrega un JWT con el scope `bank.read`.
3. El cliente envía el JWT a `bank-service`.
4. `bank-service` valida el token como Resource Server.
5. El endpoint protegido permite la operación si el scope es válido.

Flujo de eventos:

1. Se ejecuta un retiro mediante `POST /api/backend/atm/cuentas/{id}/retiro`.
2. El retiro se registra correctamente.
3. `bank-service` publica el evento `RETIRO_REALIZADO`.
4. Kafka recibe el mensaje en `bank-transacciones`.
5. El consumidor confirma la recepción del evento.

## 3. Estructura

```text
Exp3_S8_Kevin_Carrasco/
├── bank-service/
├── authorization-server/
├── config-server/
├── discovery-server/
├── docker-compose.yml
├── .env
├── README.md
├── Informe_Evidencias_S8.docx
└── Evidencias/
```

> La carpeta anterior representa la estructura esperada del proyecto. En esta documentación se entrega además el `docker-compose.yml` utilizado y un `.env.example` sin credenciales sensibles.

## 4. Tecnologías

- Java 21
- Spring Boot 3.5.5 – bank-service
- Spring Cloud 2025.0.0
- Spring Boot 4.1.1 – authorization-server
- Spring Cloud Config
- Netflix Eureka
- Spring Security
- OAuth 2.0 / JWT
- Resilience4j
- Apache Kafka 4.3.1
- Oracle XE
- Docker
- Docker Compose
- Maven
- Postman

## 5. Ejecución

### 5.1 Prerrequisitos

- Java 21
- Maven
- Docker Desktop
- Oracle XE accesible desde el host
- Kafka accesible desde el entorno donde se ejecuta `bank-service`

### 5.2 Configuración de Kafka

Crear `.env` a partir de `.env.example`:

```properties
KAFKA_BOOTSTRAP_SERVERS=184.193.34.22:9092
```

La dirección debe corresponder a la dirección accesible del broker Kafka.

### 5.3 Levantar la solución

Desde la carpeta raíz:

```powershell
docker compose up -d
```

Comprobar:

```powershell
docker compose ps
```

Los servicios esperados son:

```text
authorization-server
bank-service
config-server
discovery-server
```

### 5.4 URLs principales

```text
Eureka:
http://localhost:8761

Config Server:
http://localhost:8888

Authorization Server:
http://localhost:9000

Bank Service:
http://localhost:8091
```

## 6. Pruebas realizadas

### OAuth 2.0

Solicitud:

```text
POST http://localhost:9000/oauth2/token
```

Con:

```text
grant_type=client_credentials
scope=bank.read
```

El Authorization Server entrega un Access Token JWT.

### Endpoint protegido

```text
GET http://localhost:8091/api/backend/transacciones
```

- Con Bearer Token válido: `200 OK`.
- Sin token: `401 Unauthorized`.

### Retiro y Kafka

```text
POST http://localhost:8091/api/backend/atm/cuentas/101/retiro
```

Body:

```json
{
  "monto": 1
}
```

Resultado probado:

```text
200 OK
Retiro realizado correctamente
```

Evento generado:

```json
{
  "evento": "RETIRO_REALIZADO",
  "cuentaId": 101,
  "monto": 1
}
```

Topic:

```text
bank-transacciones
```

## 7. Resilience4j

Se implementó un Circuit Breaker para el servicio de transacciones. Se verificó mediante Actuator el estado del circuito y se realizó una prueba controlada de fallo para comprobar la activación del fallback.

## 8. Evidencias

Las capturas se encuentran en la carpeta `Evidencias/` y documentan:

1. Ejecución de los cuatro servicios con Docker Compose.
2. Registro de `BANK-SERVICE` en Eureka.
3. Acceso autorizado mediante OAuth2/JWT.
4. Rechazo de acceso sin token.
5. Conectividad con Kafka.
6. Retiro exitoso desde `bank-service`.
7. Recepción del evento `RETIRO_REALIZADO` en Kafka.

## 9. Consideraciones

- Las credenciales de Oracle y OAuth2 utilizadas durante las pruebas corresponden al entorno académico/local.
- Para un ambiente productivo se deben utilizar secretos externos y no almacenarlos directamente en archivos de configuración.
- La IP pública de Kafka puede cambiar si la instancia EC2 no utiliza una IP elástica; debe actualizarse el valor de `KAFKA_BOOTSTRAP_SERVERS` cuando corresponda.

## 10. Cumplimiento de la actividad

La solución implementa los tres puntos centrales solicitados para la Semana 8:

1. **OAuth 2.0:** Authorization Server + JWT + Resource Server.
2. **Dockerización:** imágenes Docker para los servicios principales.
3. **Docker Compose:** orquestación de los componentes de la solución mediante una única configuración.
