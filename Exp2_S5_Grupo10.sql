
-- FinanCorp - Gestión de clientes y productos financieros

-- ============================================

-- ============================================
-- CASO 1: LISTADO DE CLIENTES

-- Descripción:
-- Listar clientes trabajadores dependientes cuya profesión es CONTADOR o VENDEDOR
-- y cuyo año de inscripción es mayor al promedio redondeado de años de inscripción
-- ============================================

SELECT 
    TO_CHAR(c.numrun, '99999999') || '-' || c.dvrun AS "RUT Cliente",
    INITCAP(c.pnombre) || ' ' || INITCAP(c.appaterno) AS "Nombre Cliente",
    UPPER(po.nombre_prof_ofic) AS "Profesión Cliente",
    TO_CHAR(c.fecha_inscripcion, 'DD-MM-YYYY') AS "Fecha de Inscripción",
    INITCAP(c.direccion) AS "Dirección Cliente"
FROM 
    cliente c
    JOIN profesion_oficio po ON c.cod_prof_ofic = po.cod_prof_ofic
    JOIN tipo_cliente tc ON c.cod_tipo_cliente = tc.cod_tipo_cliente
WHERE 
    UPPER(tc.nombre_tipo_cliente) = 'TRABAJADORES DEPENDIENTES'
    AND UPPER(po.nombre_prof_ofic) IN ('CONTADOR', 'VENDEDOR')
    AND EXTRACT(YEAR FROM c.fecha_inscripcion) > (
        SELECT ROUND(AVG(EXTRACT(YEAR FROM fecha_inscripcion)))
        FROM cliente
    )
ORDER BY 
    c.numrun ASC;

-- ============================================
-- CASO 2: AUMENTO DE CRÉDITO

-- Descripción:
-- Listar RUT y edad de clientes con cupo disponible de compra
-- superior o igual al máximo cupo del año anterior
-- Crear tabla CLIENTES_CUPOS_COMPRA con los resultados
-- ============================================

-- Primero eliminar la tabla si existe
DROP TABLE clientes_cupos_compra;

-- Crear tabla con la consulta
CREATE TABLE clientes_cupos_compra AS
SELECT 
    TO_CHAR(c.numrun, '99999999') || '-' || c.dvrun AS "RUT_CLIENTE",
    TRUNC(MONTHS_BETWEEN(SYSDATE, c.fecha_nacimiento) / 12) AS "EDAD",
    TO_CHAR(t.cupo_disp_compra, '$999999999') AS "CUPO_DISPONIBLE_COMPRA",
    UPPER(tc.nombre_tipo_cliente) AS "TIPO_CLIENTE"
FROM 
    cliente c
    JOIN tarjeta_cliente t ON c.numrun = t.numrun
    JOIN tipo_cliente tc ON c.cod_tipo_cliente = tc.cod_tipo_cliente
WHERE 
    t.cupo_disp_compra >= (
        SELECT MAX(cupo_disp_compra)
        FROM tarjeta_cliente
        WHERE EXTRACT(YEAR FROM fecha_solic_tarjeta) = EXTRACT(YEAR FROM SYSDATE) - 1
    )
ORDER BY 
    TRUNC(MONTHS_BETWEEN(SYSDATE, c.fecha_nacimiento) / 12) ASC;

-- Consultar la tabla creada para verificar
SELECT * FROM clientes_cupos_compra;

-- ============================================
-- FIN DEL SCRIPT
-- ============================================