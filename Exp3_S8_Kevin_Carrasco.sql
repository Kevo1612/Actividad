SHOW PDBS;
SHOW CON_NAME;
ALTER SESSION SET CONTAINER = XEPDB1;

-- Creacion de usuarios
-- Usuario owner
CREATE USER PRY2205_USER1 IDENTIFIED BY CLAVE_USER1
DEFAULT TABLESPACE users
TEMPORARY TABLESPACE temp
QUOTA 500M ON users;
-- Usuario generico
CREATE USER PRY2205_USER2 IDENTIFIED BY CLAVE_USER2
DEFAULT TABLESPACE users
TEMPORARY TABLESPACE temp
QUOTA 300M ON users;

-- Permitir a los usuarios conectarse a la base de datos
GRANT CREATE SESSION TO PRY2205_USER1;
GRANT CREATE SESSION TO PRY2205_USER2;

-- Creacion de Roles para los usuarios con sus respectivos permisos
-- PRY2205_ROL_D
CREATE ROLE PRY2205_ROL_D;
GRANT CREATE TABLE, CREATE VIEW, CREATE SEQUENCE,
CREATE PROCEDURE TO PRY2205_ROL_D;
GRANT CREATE SYNONYM, CREATE PUBLIC SYNONYM TO PRY2205_ROL_D;
-- PRY2205_ROL_P
CREATE ROLE PRY2205_ROL_P;
GRANT CREATE SEQUENCE TO PRY2205_ROL_P;
GRANT CREATE TRIGGER TO PRY2205_ROL_P;
GRANT CREATE TABLE TO PRY2205_ROL_P;

-- Asignacion de roles a los usuarios correspondientes
GRANT PRY2205_ROL_D TO PRY2205_USER1;
GRANT PRY2205_ROL_P TO PRY2205_USER2;

-- PRY2205_USER1 otorga los permisos necesarios al Rol del usuario PRY2205_USER2
GRANT SELECT ON PRY2205_USER1.LIBRO TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.EJEMPLAR TO PRY2205_ROL_P;
GRANT SELECT ON PRY2205_USER1.PRESTAMO TO PRY2205_ROL_P;

-- Creacion de sinonimos por parte del usuario PRY2205_USER1
-- Crear sinónimos para las tablas
CREATE SYNONYM tabla_autor FOR PRY2205_USER1.autor;
CREATE SYNONYM tabla_carrera FOR PRY2205_USER1.carrera;
CREATE SYNONYM tabla_editorial FOR PRY2205_USER1.editorial;
CREATE PUBLIC SYNONYM ejemplares FOR PRY2205_USER1.ejemplar;
CREATE SYNONYM tabla_empleado FOR PRY2205_USER1.empleado;
CREATE SYNONYM tabla_escuela FOR PRY2205_USER1.escuela;
CREATE SYNONYM tabla_alumno FOR PRY2205_USER1.alumno;
CREATE PUBLIC SYNONYM prestamo FOR PRY2205_USER1.prestamo;
CREATE PUBLIC SYNONYM libro FOR PRY2205_USER1.libro;
CREATE SYNONYM tabla_valor_multa_prestamo FOR PRY2205_USER1.valor_multa_prestamo;
CREATE SYNONYM tabla_rebaja_multa FOR PRY2205_USER1.rebaja_multa;
CREATE SYNONYM tabla_detalle_prestamo_mensual FOR PRY2205_USER1.detalle_prestamo_mensual;
CREATE SYNONYM tabla_resumen_prestamo_mensual FOR PRY2205_USER1.resumen_prestamo_mensual;
CREATE SYNONYM tabla_error_proceso_prestamo FOR PRY2205_USER1.error_proceso_prestamo;

-- CASO 2: CREACIÓN DE INFORME - CONTROL_STOCK_LIBROS
-- Usuario constructor: PRY2205_USER2
CREATE TABLE CONTROL_STOCK_LIBROS AS
SELECT 
    CAST(NULL AS NUMBER) AS ID_CONTROL,
    l.libroid AS LIBRO_ID,
    l.nombre_libro AS NOMBRE_LIBRO,
    COUNT(e.ejemplarid) AS TOTAL_EJEMPLARES,
    SUM(CASE 
          WHEN p.empleadoid IN (190,180,150) 
           AND TO_CHAR(p.fecha_inicio, 'YYYY') = TO_CHAR(ADD_MONTHS(SYSDATE, -24), 'YYYY')
          THEN 1 ELSE 0 
        END) AS EN_PRESTAMO,
    COUNT(e.ejemplarid) -
    SUM(CASE 
          WHEN p.empleadoid IN (190,180,150) 
           AND TO_CHAR(p.fecha_inicio, 'YYYY') = TO_CHAR(ADD_MONTHS(SYSDATE, -24), 'YYYY')
          THEN 1 ELSE 0 
        END) AS DISPONIBLE,
    ROUND(
      (SUM(CASE 
             WHEN p.empleadoid IN (190,180,150) 
              AND TO_CHAR(p.fecha_inicio, 'YYYY') = TO_CHAR(ADD_MONTHS(SYSDATE, -24), 'YYYY')
             THEN 1 ELSE 0 
           END) / COUNT(e.ejemplarid)) * 100, 2
    ) AS PORCENTAJE_PRESTAMO,
    CASE 
      WHEN (COUNT(e.ejemplarid) -
            SUM(CASE 
                  WHEN p.empleadoid IN (190,180,150) 
                   AND TO_CHAR(p.fecha_inicio, 'YYYY') = TO_CHAR(ADD_MONTHS(SYSDATE, -24), 'YYYY')
                  THEN 1 ELSE 0 
                END)) > 2 
      THEN 'S' ELSE 'N' 
    END AS STOCK_CRITICO
FROM libro l
JOIN ejemplar e ON e.libroid = l.libroid
LEFT JOIN prestamo p ON p.libroid = l.libroid AND p.ejemplarid = e.ejemplarid
GROUP BY l.libroid, l.nombre_libro
ORDER BY l.libroid;

-- Creacion de secuencia para correlativo
CREATE SEQUENCE SEQ_CONTROL_STOCK
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;
  
-- Update del correlativo
UPDATE CONTROL_STOCK_LIBROS
SET ID_CONTROL = SEQ_CONTROL_STOCK.NEXTVAL;


-- CASO 3: OPTIMIZACIÓN DE SENTENCIAS SQL
-- Usuario constructor: PRY2205_USER2
-- CASO 3.1: CREACIÓN DE VISTA VW_DETALLE_MULTAS
CREATE OR REPLACE VIEW VW_DETALLE_MULTAS AS
SELECT 
    p.prestamoid           AS ID_PRESTAMO,
    a.nombre || ' ' || a.apaterno AS NOMBRE_ALUMNO,
    c.descripcion           AS NOMBRE_CARRERA,
    l.libroid               AS ID_LIBRO,
    TO_CHAR(l.precio, 'FM$999,999,990') AS VALOR_LIBRO,
    p.fecha_termino        AS FECHA_TERMINO,
    p.fecha_entrega        AS FECHA_ENTREGA,
    (p.fecha_entrega - p.fecha_termino) AS DIAS_ATRASO,
    
    -- Multa base: 3% del valor del libro por cada día de atraso
    TO_CHAR((l.precio * 0.03 * (p.fecha_entrega - p.fecha_termino)), 'FM$999,999,990') AS VALOR_MULTA,
    
    -- Rebaja según carrera (valores definidos en convenio)
    CASE c.carreraid
        WHEN 180 THEN 0.06
        WHEN 320 THEN 0.07
        WHEN 160 THEN 0.04
        WHEN 220 THEN 0.02
        ELSE 0
    END AS PORCENTAJE_REBAJA_MULTA,
    
    -- Multa final después de aplicar rebaja
    TO_CHAR(
      ROUND(
        (l.precio * 0.03 * (p.fecha_entrega - p.fecha_termino)) -
        (l.precio * 0.03 * (p.fecha_entrega - p.fecha_termino)) *
        CASE c.carreraid
          WHEN 180 THEN 0.06
          WHEN 320 THEN 0.07
          WHEN 160 THEN 0.04
          WHEN 220 THEN 0.02
          ELSE 0
        END
      ), 'FM$999,999,990'
    ) AS VALOR_REBAJADO
FROM prestamo p
JOIN alumno a ON a.alumnoid = p.alumnoid
JOIN carrera c ON c.carreraid = a.carreraid
JOIN libro l ON l.libroid = p.libroid
WHERE TO_CHAR(p.fecha_termino, 'YYYY') = TO_CHAR(ADD_MONTHS(SYSDATE, -24), 'YYYY')
  AND p.fecha_entrega > p.fecha_termino
ORDER BY p.fecha_entrega DESC;

-- CASO 3.2: CREACIÓN DE ÍNDICES
-- Índices para joins
CREATE INDEX IDX_PRESTAMO_ALUMNO ON PRY2205_USER1.prestamo(alumnoid);
CREATE INDEX IDX_PRESTAMO_LIBRO ON PRY2205_USER1.prestamo(libroid);
CREATE INDEX IDX_ALUMNO_CARRERA ON PRY2205_USER1.alumno(carreraid);

-- Índices para filtros y ordenamiento
CREATE INDEX IDX_PRESTAMO_FECHA_TERMINO ON PRY2205_USER1.prestamo(fecha_termino);
CREATE INDEX IDX_PRESTAMO_FECHA_ENTREGA ON PRY2205_USER1.prestamo(fecha_entrega);

-- Índice compuesto
CREATE INDEX IDX_PRESTAMO_FECHAS ON PRY2205_USER1.prestamo(fecha_termino, fecha_entrega);