-- CASO 1: LISTADO DE TRABAJADORES
SELECT
    INITCAP(t.nombre || ' ' || t.appaterno || ' ' || t.apmaterno) AS "Nombre Completo Trabajador",
    TO_CHAR(TRUNC(t.numrut / 1000), 'FM99G999G999') || '-' || t.dvrut AS "RUT Trabajador",
    UPPER(tt.desc_categoria) AS "Tipo Trabajador",
    INITCAP(cc.nombre_ciudad) AS "Ciudad Trabajador",
    '$' || TO_CHAR(t.sueldo_base, 'FM999G999G999') AS "Sueldo Base"
FROM 
    trabajador t
    INNER JOIN tipo_trabajador tt ON t.id_categoria_t = tt.id_categoria
    INNER JOIN comuna_ciudad cc ON t.id_ciudad = cc.id_ciudad
WHERE 
    t.sueldo_base BETWEEN 650000 AND 3000000
ORDER BY 
    cc.nombre_ciudad DESC, t.sueldo_base ASC;



-- CASO 2: LISTADO DE CAJEROS CON VENTAS (CORREGIDO)
SELECT
    TO_CHAR(TRUNC(t.numrut / 1000), 'FM99G999G999') || '-' || t.dvrut AS "RUT Trabajador",
    INITCAP(t.nombre || ' ' || t.appaterno || ' ' || t.apmaterno) AS "Nombre Trabajador",
    COUNT(tc.nro_ticket) AS "Total Tickets",
    '$' || TO_CHAR(SUM(tc.monto_ticket), 'FM999G999G999') AS "Total Vendido",
    '$' || TO_CHAR(SUM(ct.valor_comision), 'FM999G999G999') AS "Comisión Total",
    UPPER(tt.desc_categoria) AS "Tipo Trabajador",
    INITCAP(cc.nombre_ciudad) AS "Ciudad Trabajador"
FROM 
    trabajador t
    INNER JOIN tipo_trabajador tt ON t.id_categoria_t = tt.id_categoria
    INNER JOIN tickets_concierto tc ON t.numrut = tc.numrut_t
    INNER JOIN comisiones_ticket ct ON tc.nro_ticket = ct.nro_ticket
    INNER JOIN comuna_ciudad cc ON t.id_ciudad = cc.id_ciudad
WHERE 
    UPPER(tt.desc_categoria) = 'CAJERO'
GROUP BY 
    t.numrut, t.dvrut, t.nombre, t.appaterno, t.apmaterno, tt.desc_categoria, cc.nombre_ciudad
HAVING 
    SUM(tc.monto_ticket) > 50000
ORDER BY 
    SUM(tc.monto_ticket) DESC;


-- CASO 3: LISTADO DE BONIFICACIONES
SELECT
    TO_CHAR(TRUNC(t.numrut / 1000), 'FM99G999G999') || '-' || t.dvrut AS "RUT Trabajador",
    INITCAP(t.nombre || ' ' || t.appaterno) AS "Trabajador Nombre",
    EXTRACT(YEAR FROM t.fecing) AS "Año Ingreso",
    TRUNC(MONTHS_BETWEEN(SYSDATE, t.fecing) / 12) AS "Años Antigüedad",
    NVL((SELECT COUNT(*) FROM asignacion_familiar af WHERE af.numrut_t = t.numrut), 0) AS "Num. Cargas Familiares",
    INITCAP(i.nombre_isapre) AS "Nombre Isapre",
    '$' || TO_CHAR(t.sueldo_base, 'FM999G999G999') AS "Sueldo Base",
    CASE 
        WHEN UPPER(i.nombre_isapre) = 'FONASA' 
        THEN '$' || TO_CHAR(ROUND(t.sueldo_base * 0.01), 'FM999G999')
        ELSE '$0'
    END AS "Bono Fonasa",
    CASE 
        WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, t.fecing) / 12) <= 10 
        THEN '$' || TO_CHAR(ROUND(t.sueldo_base * 0.10), 'FM999G999')
        ELSE '$' || TO_CHAR(ROUND(t.sueldo_base * 0.15), 'FM999G999')
    END AS "Bono Antigüedad",
    INITCAP(a.nombre_afp) AS "Nombre Afp",
    INITCAP(ec.desc_estcivil) AS "Estado Civil"
FROM 
    trabajador t
    LEFT JOIN isapre i ON t.cod_isapre = i.cod_isapre
    LEFT JOIN afp a ON t.cod_afp = a.cod_afp
    LEFT JOIN est_civil eciv ON t.numrut = eciv.numrut_t
    LEFT JOIN estado_civil ec ON eciv.id_estcivil_est = ec.id_estcivil
WHERE 
    (eciv.fecter_estcivil IS NULL OR eciv.fecter_estcivil > SYSDATE)
ORDER BY 
    t.numrut ASC;