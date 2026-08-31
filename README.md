# Proyecto Spring Batch - Procesamiento Bancario

## Descripción

Proyecto desarrollado con **Spring Boot y Spring Batch** para procesar datos bancarios provenientes de archivos CSV, aplicando validaciones, transformaciones y tolerancia a errores antes de almacenarlos en **Oracle Database**.

## Jobs implementados

- **Transacciones:** procesa `transacciones.csv` y almacena las transacciones válidas.
- **Intereses:** procesa `intereses.csv` y genera los registros de intereses.
- **Estados de cuenta:** procesa `cuentas_anuales.csv` y genera los estados de cuenta anuales.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Batch
- Maven
- Oracle Database
- JPA / Hibernate

## Estructura

```text
src/main/java/com/example/demo/
├── batch/
│   ├── config/
│   ├── transacciones/
│   ├── intereses/
│   └── estadoscuenta/
├── dto/
├── model/
└── repository/
