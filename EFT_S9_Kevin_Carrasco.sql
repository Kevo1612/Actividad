SHOW PDBS;
SHOW CON_NAME;
ALTER SESSION SET CONTAINER = XEPDB1;

-- Caso 1 Creacion de usuarios, roles y privilegios
-- Desde usuario ADMIN
SHOW USER; -- Verificar usuario actual
-- ============================================
-- Usuario EFT (OWNER)
-- ============================================
CREATE USER PRY2205_EFT
IDENTIFIED BY "ClaveOwner2025"
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA 50M ON USERS;

-- Privilegios básicos
GRANT CREATE SESSION TO PRY2205_EFT;

-- Privilegios para crear objetos
GRANT CREATE TABLE, CREATE VIEW, CREATE SEQUENCE, CREATE SYNONYM, CREATE PUBLIC SYNONYM TO PRY2205_EFT;
GRANT CREATE INDEXTYPE TO PRY2205_EFT;

-- ============================================
-- Usuario DES
-- ============================================
CREATE USER PRY2205_EFT_DES
IDENTIFIED BY "ClaveDes2025"
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA 50M ON USERS;

-- Privilegios conexion 
GRANT CREATE SESSION TO PRY2205_EFT_DES;

-- Privilegios administrativos solicitados
GRANT CREATE USER, CREATE PROFILE TO PRY2205_EFT_DES;

-- Rol de desarrollo
CREATE ROLE PRY2205_ROL_D;

-- Privilegios del rol D
GRANT CREATE TABLE, CREATE VIEW, TO PRY2205_ROL_D;

-- Asignar rol al usuario DES
GRANT PRY2205_ROL_D TO PRY2205_EFT_DES;

-- ============================================
-- Usuario CON
-- ============================================
CREATE USER PRY2205_EFT_CON
IDENTIFIED BY "ClaveCon2025"
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA 50M ON USERS;

-- Privilegio conexion
GRANT CREATE SESSION TO PRY2205_EFT_CON;

-- Rol de consulta
CREATE ROLE PRY2205_ROL_C;

-- Asignar rol al usuario CON
GRANT PRY2205_ROL_C TO PRY2205_EFT_CON;

-- Desde el usuario OWNER
-- ============================================
-- Caso 1
-- ============================================
-- El usuario PRY2205_EFT crea los sinónimos
-- Y da el acceso correspondiente a lo demas usuarios

-- Sinónimos públicos para que usuario CON pueda ver la vista
CREATE OR REPLACE PUBLIC SYNONYM syn_asesoria         FOR ASESORIA;
CREATE OR REPLACE PUBLIC SYNONYM syn_empresa          FOR EMPRESA;

-- Sinonimos publicos para el caso 2
CREATE OR REPLACE PUBLIC SYNONYM syn_cartola          FOR CARTOLA_PROFESIONALES;
CREATE OR REPLACE PUBLIC SYNONYM syn_profesional      FOR PROFESIONAL;
CREATE OR REPLACE PUBLIC SYNONYM syn_profesion        FOR PROFESION;
CREATE OR REPLACE PUBLIC SYNONYM syn_isapre           FOR ISAPRE;
CREATE OR REPLACE PUBLIC SYNONYM syn_tipocontrato     FOR TIPO_CONTRATO;
CREATE OR REPLACE PUBLIC SYNONYM syn_rangosueldo      FOR RANGOS_SUELDOS;

-- Permisos para que usuario DES pueda crear caso 2
GRANT INSERT ON CARTOLA_PROFESIONALES TO PRY2205_EFT_DES;
GRANT SELECT ON CARTOLA_PROFESIONALES TO PRY2205_EFT_DES;
GRANT SELECT ON PROFESIONAL TO PRY2205_EFT_DES;
GRANT SELECT ON PROFESION TO PRY2205_EFT_DES;
GRANT SELECT ON ISAPRE TO PRY2205_EFT_DES;
GRANT SELECT ON TIPO_CONTRATO TO PRY2205_EFT_DES;
GRANT SELECT ON RANGOS_SUELDOS TO PRY2205_EFT_DES;

-- Permiso para que usuario CON pueda ver la
-- tabla CARTOLA_PROFESIONALES usanso el sinonimo
GRANT SELECT ON CARTOLA_PROFESIONALES TO PRY2205_EFT_CON;

-- Permisos para que el usuario CON pueda ver la vista
GRANT SELECT ON ASESORIA TO PRY2205_EFT_CON;
GRANT SELECT ON EMPRESA TO PRY2205_EFT_CON;
GRANT SELECT ON VW_EMPRESAS_ASESORADAS TO PRY2205_EFT_CON;


-- ============================================
-- Caso 2 Creacion de informe guardado en CARTOLA_PROFESIONALES
-- ============================================
-- Desde usuario DES
SHOW USER; -- Verificar usuario actual
INSERT INTO syn_cartola (
    RUT_PROFESIONAL,
    NOMBRE_PROFESIONAL,
    PROFESION,
    ISAPRE,
    SUELDO_BASE,
    PORC_COMISION_PROFESIONAL,
    VALOR_TOTAL_COMISION,
    PORCENTATE_HONORARIO,
    BONO_MOVILIZACION,
    TOTAL_PAGAR
)
SELECT 
    p.RUTPROF AS RUT_PROFESIONAL,
    INITCAP(p.NOMPRO || ' ' || p.APPPRO || ' ' || p.APMPRO) AS NOMBRE_PROFESIONAL,
    pr.NOMPROFESION AS PROFESION,
    i.NOMISAPRE AS ISAPRE,
    p.SUELDO AS SUELDO_BASE,
    NVL(p.COMISION,0) AS PORC_COMISION_PROFESIONAL,
    NVL(p.COMISION,0) * p.SUELDO AS VALOR_TOTAL_COMISION,
    ROUND(p.SUELDO * NVL(rh.HONOR_PCT,0) / 100, 0) AS PORCENTATE_HONORARIO,
    
    CASE tc.IDTCONTRATO
        WHEN 1 THEN 150000   -- Indefinido Jornada Completa
        WHEN 2 THEN 120000   -- Indefinido Jornada Parcial
        WHEN 3 THEN 60000    -- Plazo fijo
        WHEN 4 THEN 50000    -- Honorarios
        ELSE 0
    END AS BONO_MOVILIZACION,
    
    p.SUELDO
    + NVL(p.COMISION,0) * p.SUELDO
    + NVL(rh.HONOR_PCT,0) * p.SUELDO / 100
    + CASE tc.IDTCONTRATO
        WHEN 1 THEN 150000   -- Indefinido Jornada Completa
        WHEN 2 THEN 120000   -- Indefinido Jornada Parcial
        WHEN 3 THEN 60000    -- Plazo fijo
        WHEN 4 THEN 50000    -- Honorarios
        ELSE 0
      END AS TOTAL_PAGAR

FROM syn_profesional p
JOIN syn_profesion pr ON p.IDPROFESION = pr.IDPROFESION
JOIN syn_isapre i ON p.IDISAPRE = i.IDISAPRE
JOIN syn_tipocontrato tc ON p.IDTCONTRATO = tc.IDTCONTRATO
LEFT JOIN syn_rangosueldo rh ON p.SUELDO BETWEEN rh.S_MIN AND rh.S_MAX
ORDER BY pr.NOMPROFESION, p.SUELDO DESC, p.COMISION, p.RUTPROF;
COMMIT; -- Confirmar los cambios para que sean visibles en la tabla

SELECT * FROM syn_cartola; -- Verificar los datos insertados desde sinónimo público

-- Desde usuario CON
-- Verificar usuario
SHOW USER; 

-- Ver tabla CARTOLA_PROFESIONALES desde el sinonimo syn_cartola desde el usuario CON
SELECT * FROM syn_cartola;

-- ============================================
-- Caso 3
-- ============================================
-- Desde usuario OWNER
SHOW USER; -- Verificar usuario actual
-- Caso 3.1: Creación de la vista VW_EMPRESAS_ASESORADAS
CREATE OR REPLACE VIEW VW_EMPRESAS_ASESORADAS AS
SELECT 
    TO_CHAR(e.RUT_EMPRESA, 'FM999G999G999') || '-' || e.DV_EMPRESA AS RUT_EMPRESA,
    UPPER(e.NOMEMPRESA) AS NOMBRE_EMPRESA,
    e.IVA_DECLARADO AS IVA,
    
    -- Años de antigüedad calculados desde la fecha de inicio de actividades
    TRUNC(MONTHS_BETWEEN(SYSDATE, e.FECHA_INICIACION_ACTIVIDADES) / 12) AS ANIOS_EXISTENCIA,

    -- Promedio mensual de asesorías
    ROUND(COUNT(a.IDEMPRESA) / 12) AS PROMEDIO_ASESORIAS,
    
    -- Devolución de IVA estimada
    ROUND(e.IVA_DECLARADO * (COUNT(a.IDEMPRESA) / 12) / 100, 0) AS DEVOLUCION_IVA,
    
    -- Clasificación del cliente
    CASE 
        WHEN ROUND(COUNT(a.IDEMPRESA) / 12) > 5 THEN 'CLIENTE PREMIUM'
        WHEN ROUND(COUNT(a.IDEMPRESA) / 12) BETWEEN 3 AND 5 THEN 'CLIENTE'
        ELSE 'CLIENTE POCO CONCURRIDO'
    END AS TIPO_CLIENTE,
    
    -- Promociones o recomendaciones
    CASE 
        WHEN ROUND(COUNT(a.IDEMPRESA) / 12) > 5 AND COUNT(a.IDEMPRESA) >= 7 THEN '1 ASESORIA GRATIS'
        WHEN ROUND(COUNT(a.IDEMPRESA) / 12) > 5 AND COUNT(a.IDEMPRESA) < 7 THEN '1 ASESORIA 40% DE DESCUENTO'
        WHEN ROUND(COUNT(a.IDEMPRESA) / 12) BETWEEN 3 AND 5 AND COUNT(a.IDEMPRESA) = 5 THEN '1 ASESORIA 30% DE DESCUENTO'
        WHEN ROUND(COUNT(a.IDEMPRESA) / 12) BETWEEN 3 AND 5 AND COUNT(a.IDEMPRESA) < 5 THEN '1 ASESORIA 20% DE DESCUENTO'
        ELSE 'CAPTAR CLIENTE'
    END AS CORRESPONDE

FROM syn_empresa e
JOIN syn_asesoria a ON e.IDEMPRESA = a.IDEMPRESA
WHERE EXTRACT(YEAR FROM a.FIN) = EXTRACT(YEAR FROM SYSDATE) - 1
GROUP BY e.RUT_EMPRESA, e.DV_EMPRESA, e.NOMEMPRESA, e.FECHA_INICIACION_ACTIVIDADES, e.IVA_DECLARADO
ORDER BY INITCAP(e.NOMEMPRESA) ASC;

SELECT * FROM VW_EMPRESAS_ASESORADAS;

-- Desde usuario CON
-- Verificar usuario
SHOW USER;
-- Ver vista creada con usuario OWNER desde usuario CON
SELECT * FROM PRY2205_EFT.VW_EMPRESAS_ASESORADAS;

-- Caso 3.2: Indices
-- Desde usuario OWNER
SHOW USER; -- Verificar usuario actual

EXPLAIN PLAN FOR
SELECT * 
FROM VW_EMPRESAS_ASESORADAS;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

CREATE INDEX IDX_ASESORIA_IDEMPRESA ON ASESORIA(IDEMPRESA);
CREATE INDEX IDX_ASESORIA_FIN_YEAR ON ASESORIA(EXTRACT(YEAR FROM FIN));
CREATE INDEX IDX_ASESORIA_EMPRESA_FIN ON ASESORIA(IDEMPRESA, FIN);