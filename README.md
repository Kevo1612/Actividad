# Banco XYZ - Backend for Frontend (BFF)

## Objetivo del proyecto

El objetivo de este proyecto es implementar el patrón arquitectónico **Backend for Frontend (BFF)** para el sistema del Banco XYZ.

La solución permite adaptar la información y funcionalidades del backend según las necesidades de cada tipo de cliente, implementando tres BFF independientes:

- **BFF Web:** orientado a clientes que utilizan un navegador y requieren información más completa.
- **BFF Mobile:** orientado a dispositivos móviles, entregando respuestas más livianas y con la información esencial.
- **BFF ATM:** orientado a cajeros automáticos, proporcionando operaciones críticas como consulta de saldo y retiro de dinero.

Los tres BFF se comunican con un **Backend central**, el cual gestiona el acceso a la información almacenada en Oracle Database.

---

## Estructura del código

El proyecto está organizado en cuatro aplicaciones independientes:

```
BancoXYZ-BFF/
│
├── backend/
│   └── Backend central
│
├── bff-web/
│   └── BFF para clientes Web
│
├── bff-mobile/
│   └── BFF para clientes Mobile
│
├── bff-atm/
│   └── BFF para cajeros ATM
│
└── README.md
```

Cada aplicación Spring Boot utiliza una estructura organizada por responsabilidades:

```
src/main/java/com/example/bancoxyz/
│
├── client/
├── controller/
├── dto/
├── model/
├── repository/
└── service/
```

### Descripción de las capas

- **controller:** recibe y gestiona las solicitudes HTTP.
- **service:** contiene la lógica de procesamiento y adaptación de la información.
- **client:** permite la comunicación entre los BFF y el Backend central.
- **dto:** define los objetos de respuesta y solicitud utilizados por cada canal.
- **model:** representa las entidades utilizadas por la aplicación.
- **repository:** permite el acceso a la información de la base de datos.

Los BFF utilizan DTO específicos para cada canal, permitiendo entregar respuestas adaptadas a las necesidades de Web, Mobile y ATM.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Maven
- Oracle Database
- REST API
- Lombok
- Jakarta Validation
- Postman

---

## Puertos utilizados

| Aplicación | Puerto |
|------------|-------:|
| Backend central | 8091 |
| BFF Web | 8081 |
| BFF Mobile | 8082 |
| BFF ATM | 8083 |

---

## Requisitos previos

Para ejecutar el proyecto se necesita:

- Java 21 instalado.
- Maven o Maven Wrapper incluido en cada proyecto.
- Oracle Database disponible.
- Base de datos configurada con las tablas utilizadas por el proyecto.
- Postman para realizar las pruebas de las APIs.

---

## Configuración de la base de datos

Los proyectos utilizan Oracle Database mediante una conexión similar a:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=BANK_BATCH
spring.datasource.password=PASSWORD123
```


---

# Instrucciones para ejecutar el proyecto

## 1. Ejecutar el Backend central

Ingresar a la carpeta:

```
backend/
```

En Windows ejecutar:

```cmd
mvnw.cmd spring-boot:run
```

El Backend quedará disponible en:

```
http://localhost:8091
```

## 2. Ejecutar BFF Web

Ingresar a:

```
bff-web/
```

Ejecutar:

```cmd
mvnw.cmd spring-boot:run
```

El BFF Web quedará disponible en:

```
http://localhost:8081
```

## 3. Ejecutar BFF Mobile

Ingresar a:

```
bff-mobile/
```

Ejecutar:

```cmd
mvnw.cmd spring-boot:run
```

El BFF Mobile quedará disponible en:

```
http://localhost:8082
```

## 4. Ejecutar BFF ATM

Ingresar a:

```
bff-atm/
```

Ejecutar:

```cmd
mvnw.cmd spring-boot:run
```

El BFF ATM quedará disponible en:

```
http://localhost:8083
```

---

## Orden recomendado de ejecución

Se recomienda iniciar primero el Backend central y posteriormente los tres BFF:

```
1. Backend      → 8091
2. BFF Web      → 8081
3. BFF Mobile   → 8082
4. BFF ATM      → 8083
```

De esta forma, los BFF pueden comunicarse correctamente con el Backend central.

---

## Ejemplos de endpoints

### BFF Web

```
GET http://localhost:8081/api/web/cuentas/119
GET http://localhost:8081/api/web/estado-cuenta/119
GET http://localhost:8081/api/web/transacciones
GET http://localhost:8081/api/web/cuentas/119/resumen-completo
```

### BFF Mobile

```
GET http://localhost:8082/api/mobile/cuentas/119/resumen
GET http://localhost:8082/api/mobile/cuentas/119/saldo
GET http://localhost:8082/api/mobile/transacciones/ultimas
```

### BFF ATM

Consultar saldo:

```
GET http://localhost:8083/api/atm/cuentas/119/saldo
```

Realizar retiro:

```
POST http://localhost:8083/api/atm/cuentas/119/retiro
```

Body:

```json
{
    "monto": 100
}
```

---

## Pruebas

Las APIs fueron probadas utilizando **Postman**, verificando respuestas HTTP `200 OK` en las operaciones principales de los tres BFF.

También se verificó el registro de las operaciones de retiro realizadas mediante el BFF ATM en la tabla:

```
ATM_OPERACIONES
```

---

## Resultado

La implementación permite que cada canal utilice un BFF especializado, mientras que el Backend central concentra el acceso a los datos y las funcionalidades principales del sistema.

De esta manera, Web, Mobile y ATM reciben información adaptada a sus necesidades específicas.
