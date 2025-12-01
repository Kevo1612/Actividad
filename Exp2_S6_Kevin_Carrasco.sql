-- Caso 1
SELECT 
       t.ID_PROFESIONAL                                      AS ID,
       UPPER(t.APPATERNO || ' ' ||
       t.APMATERNO || ' ' || t.NOMBRE)                       AS PROFESIONAL,
       SUM(t.CANT_BANCA)                                     AS "NRO ASESORIA BANCA",
       TO_CHAR(SUM(t.MONTO_BANCA), '$999G999G999')           AS "MONTO_TOTAL_BANCA",
       SUM(t.CANT_RETAIL)                                    AS "NRO ASESORIAS RETAIL",
       TO_CHAR(SUM(t.MONTO_RETAIL), '$999G999G999')          AS "MONTO_TOTAL_RETAIL",
       (SUM(t.CANT_BANCA) + SUM(t.CANT_RETAIL))              AS TOTAL_ASESORIAS,
       TO_CHAR((SUM(t.MONTO_BANCA) + 
       SUM(t.MONTO_RETAIL)), '$999G999G999')                 AS TOTAL_HONORARIOS
FROM 
(
    -- Subconsulta para SECTOR BANCA (CÓDIGO 3)
    SELECT 
           p.ID_PROFESIONAL,
           p.APPATERNO,
           p.APMATERNO,
           p.NOMBRE,
           COUNT(a.ID_PROFESIONAL)        AS CANT_BANCA,
           NVL(SUM(a.HONORARIO),0)        AS MONTO_BANCA,
           0                              AS CANT_RETAIL,
           0                              AS MONTO_RETAIL
    FROM PROFESIONAL p
         JOIN ASESORIA a 
              ON p.ID_PROFESIONAL = a.ID_PROFESIONAL
         JOIN EMPRESA e
              ON a.COD_EMPRESA = e.COD_EMPRESA
    WHERE e.COD_SECTOR = 3   -- BANCA
    GROUP BY p.ID_PROFESIONAL, p.APPATERNO, p.APMATERNO, p.NOMBRE

    UNION ALL

    -- Subconsulta para SECTOR RETAIL (CÓDIGO 4)
    SELECT 
           p.ID_PROFESIONAL,
           p.APPATERNO,
           p.APMATERNO,
           p.NOMBRE,
           0                              AS CANT_BANCA,
           0                              AS MONTO_BANCA,
           COUNT(a.ID_PROFESIONAL)        AS CANT_RETAIL,
           NVL(SUM(a.HONORARIO),0)        AS MONTO_RETAIL
    FROM PROFESIONAL p
         JOIN ASESORIA a 
              ON p.ID_PROFESIONAL = a.ID_PROFESIONAL
         JOIN EMPRESA e
              ON a.COD_EMPRESA = e.COD_EMPRESA
    WHERE e.COD_SECTOR = 4   -- RETAIL
    GROUP BY p.ID_PROFESIONAL, p.APPATERNO, p.APMATERNO, p.NOMBRE
) t
GROUP BY 
       t.ID_PROFESIONAL,
       t.APPATERNO,
       t.APMATERNO,
       t.NOMBRE
HAVING 
       SUM(t.CANT_BANCA) > 0
   AND SUM(t.CANT_RETAIL) > 0   -- profesionales que han trabajado en ambos sectores
ORDER BY
       ID_PROFESIONAL ASC;
       
--Caso 2
-- Creacion de la tabla de reporte para almacenar la informacion 
DROP TABLE REPORTE_MES CASCADE CONSTRAINTS;
CREATE TABLE REPORTE_MES (
    ID_PROF                       NUMBER(10),
    NOMBRE_COMPLETO               VARCHAR2(60),
    NOMBRE_PROFESION              VARCHAR2(30),
    NOM_COMUNA                    VARCHAR2(30),
    NRO_ASESORIAS                 NUMBER(6),
    MONTO_TOTAL_HONORARIOS        NUMBER(12),
    PROMEDIO_HONORARIO            NUMBER(12),
    HONORARIO_MINIMO              NUMBER(12),
    HONORARIO_MAXIMO              NUMBER(12)
);

-- Consulta de INSERT para la tabla de reporte
INSERT INTO REPORTE_MES
(
    ID_PROF,
    NOMBRE_COMPLETO,
    NOMBRE_PROFESION,
    NOM_COMUNA,
    NRO_ASESORIAS,
    MONTO_TOTAL_HONORARIOS,
    PROMEDIO_HONORARIO,
    HONORARIO_MINIMO,
    HONORARIO_MAXIMO
)
SELECT
    p.ID_PROFESIONAL                            AS ID_PROFESIONAL,
    INITCAP((p.APPATERNO || ' ' || 
    p.APMATERNO || ' ' || p.NOMBRE))            AS NOMBRE_COMPLETO,
    INITCAP(pr.NOMBRE_PROFESION)                AS NOMBRE_PROFESION,
    INITCAP(c.NOM_COMUNA)                       AS NOM_COMUNA,
    COUNT(a.HONORARIO)                          AS NRO_ASESORIAS,
    ROUND(NVL(SUM(a.HONORARIO),0),0)            AS MONTO_TOTAL_HONORARIOS,
    ROUND(AVG(a.HONORARIO),0)                   AS PROMEDIO_HONORARIO,
    ROUND(MIN(a.HONORARIO),0)                   AS HONORARIO_MINIMO,
    ROUND(MAX(a.HONORARIO),0)                   AS HONORARIO_MAXIMO

FROM PROFESIONAL p
     JOIN PROFESION pr      ON p.COD_PROFESION = pr.COD_PROFESION
     JOIN COMUNA c          ON p.COD_COMUNA = c.COD_COMUNA
     JOIN ASESORIA a        ON p.ID_PROFESIONAL = a.ID_PROFESIONAL

-- Restricción de datos: asesorías finalizadas en abril del año pasado
WHERE 
      a.FIN_ASESORIA BETWEEN 
          ADD_MONTHS(TRUNC(SYSDATE,'YYYY'), -9)
      AND LAST_DAY(ADD_MONTHS(TRUNC(SYSDATE,'YYYY'), -9))

GROUP BY 
      p.ID_PROFESIONAL,
      p.APPATERNO,
      p.APMATERNO,
      p.NOMBRE,
      pr.NOMBRE_PROFESION,
      c.NOM_COMUNA

ORDER BY 
      p.ID_PROFESIONAL ASC;
      
SELECT * FROM REPORTE_MES;

-- Caso 3
-- Consulta para mostrar a los profesionales que cumplan con
-- la condición para el aumento de sueldo.
-- Se puede usar para antes y despues de la actualización.
SELECT 
    NVL(SUM(a.HONORARIO), 0) AS HONORARIO,
    p.ID_PROFESIONAL,
    p.NUMRUN_PROF,
    p.SUELDO AS SUELDO
FROM PROFESIONAL p
LEFT JOIN ASESORIA a 
    ON p.ID_PROFESIONAL = a.ID_PROFESIONAL
WHERE TO_CHAR(a.FIN_ASESORIA, 'MMYYYY') = '032021'
GROUP BY p.ID_PROFESIONAL, p.NUMRUN_PROF, p.SUELDO;

-- Calculo para actualizar el sueldo de los profesionales
UPDATE PROFESIONAL p
SET SUELDO = SUELDO + 
    CASE 
        WHEN (
            SELECT NVL(SUM(a.HONORARIO), 0)
            FROM ASESORIA a
            WHERE a.ID_PROFESIONAL = p.ID_PROFESIONAL
              AND TO_CHAR(a.FIN_ASESORIA, 'MMYYYY') = '032021'
        ) >= 1000000 THEN ROUND(SUELDO * 0.15) -- Aumento de 15% por superar o igualar el millon de acumulacion de honorarios.
        
        WHEN (
            SELECT NVL(SUM(a.HONORARIO), 0)
            FROM ASESORIA a
            WHERE a.ID_PROFESIONAL = p.ID_PROFESIONAL
              AND TO_CHAR(a.FIN_ASESORIA, 'MMYYYY') = '032021'
        ) < 1000000 THEN ROUND(SUELDO * 0.10) -- Aumento de 10% por acumulacion de honorarios menor al millon. 
        
        ELSE 0
    END
WHERE EXISTS (
    SELECT 1
    FROM ASESORIA a
    WHERE a.ID_PROFESIONAL = p.ID_PROFESIONAL
      AND TO_CHAR(a.FIN_ASESORIA, 'MMYYYY') = '032021'
);

