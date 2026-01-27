------------------------------------------------------------
-- CASO 1: Procesar pagos morosos de pacientes
------------------------------------------------------------
DECLARE
    -- Año de acreditación (paramétrico)
    v_anno_acreditacion NUMBER := 2026;
    v_anno_proceso      NUMBER := v_anno_acreditacion - 1;

    -- Cursor explícito: atenciones pagadas fuera de plazo del año anterior
    CURSOR c_morosidad IS
        SELECT
            p.pac_run,
            p.dv_run,
            p.pnombre || ' ' || p.snombre || ' ' || p.apaterno || ' ' || p.amaterno AS pac_nombre,
            a.ate_id,
            a.esp_id,
            pa.fecha_venc_pago,
            pa.fecha_pago,
            NVL(pa.fecha_pago, SYSDATE) - pa.fecha_venc_pago AS dias_morosidad,
            e.nombre AS especialidad,
            p.apaterno,
            p.fecha_nacimiento
        FROM paciente p
             JOIN atencion      a ON p.pac_run = a.pac_run
             JOIN pago_atencion pa ON a.ate_id = pa.ate_id
             JOIN especialidad  e ON a.esp_id = e.esp_id
        WHERE (pa.fecha_pago IS NULL OR pa.fecha_pago > pa.fecha_venc_pago)
          AND EXTRACT(YEAR FROM pa.fecha_venc_pago) = v_anno_proceso
        ORDER BY pa.fecha_venc_pago, p.apaterno;

    -- Registro PL/SQL para almacenar cada fila del cursor
    TYPE r_morosidad IS RECORD (
        pac_run          paciente.pac_run%TYPE,
        dv_run           paciente.dv_run%TYPE,
        pac_nombre       VARCHAR2(100),
        ate_id           atencion.ate_id%TYPE,
        esp_id           atencion.esp_id%TYPE,
        fecha_venc_pago  DATE,
        fecha_pago       DATE,
        dias_morosidad   NUMBER,
        especialidad     VARCHAR2(100),
        apaterno         paciente.apaterno%TYPE,
        fecha_nacimiento paciente.fecha_nacimiento%TYPE
    );
    v_reg r_morosidad;

    -- VARRAY de multas por día según especialidad
    TYPE t_multas IS VARRAY(10) OF NUMBER;
    v_multas t_multas := t_multas(1200, 1300, 1700, 1900, 1100, 2000, 2300);

    -- Variables auxiliares
    v_multa_dia   NUMBER;
    v_multa_total NUMBER;
    v_descuento   NUMBER := 0;
    v_edad        NUMBER;
BEGIN
    -- Limpiar tabla destino antes de cargar nuevos datos
    EXECUTE IMMEDIATE 'TRUNCATE TABLE pago_moroso';

    OPEN c_morosidad;
    LOOP
        FETCH c_morosidad INTO v_reg;
        EXIT WHEN c_morosidad%NOTFOUND;

        ------------------------------------------------------------
        -- Determinar multa diaria según especialidad
        ------------------------------------------------------------
        CASE v_reg.esp_id
            WHEN 100 THEN  -- Cirugía General
                v_multa_dia := v_multas(1);

            WHEN 200 THEN  -- Ortopedia y Traumatología
                v_multa_dia := v_multas(2);

            WHEN 300 THEN  -- Dermatología
                v_multa_dia := v_multas(1);

            WHEN 400 THEN  -- Inmunología
                v_multa_dia := v_multas(3);

            WHEN 500 THEN  -- Fisiatría
                v_multa_dia := v_multas(4);

            WHEN 600 THEN  -- Medicina Interna
                v_multa_dia := v_multas(4);

            WHEN 700 THEN  -- Medicina General
                v_multa_dia := v_multas(5);

            WHEN 900 THEN  -- Otorrinolaringología
                v_multa_dia := v_multas(3);

            WHEN 1100 THEN -- Psiquiatría Adultos
                v_multa_dia := v_multas(6);

            WHEN 1400 THEN -- Cirugía Digestiva
                v_multa_dia := v_multas(7);

            WHEN 1800 THEN -- Reumatología
                v_multa_dia := v_multas(7);

            ELSE           -- Otras especialidades (valor por defecto)
                v_multa_dia := 1000;
        END CASE;

        ------------------------------------------------------------
        -- Calcular multa total y aplicar descuentos
        ------------------------------------------------------------
        v_multa_total := v_reg.dias_morosidad * v_multa_dia;

        -- Calcular edad exacta en años
        v_edad := TRUNC((SYSDATE - v_reg.fecha_nacimiento) / 365.25);

        -- Descuento por tercera edad (según tabla de tramos)
        SELECT NVL(MAX(porcentaje_descto), 0)
        INTO v_descuento
        FROM porc_descto_3ra_edad
        WHERE v_edad BETWEEN anno_ini AND anno_ter;

        -- Aplicar descuento
        v_multa_total := v_multa_total - (v_multa_total * v_descuento / 100);

        ------------------------------------------------------------
        -- Insertar registro en tabla PAGO_MOROSO
        ------------------------------------------------------------
        INSERT INTO pago_moroso (
            pac_run,
            pac_dv_run,
            pac_nombre,
            ate_id,
            fecha_venc_pago,
            fecha_pago,
            dias_morosidad,
            especialidad_atencion,
            monto_multa
        ) VALUES (
            v_reg.pac_run,
            v_reg.dv_run,
            v_reg.pac_nombre,
            v_reg.ate_id,
            v_reg.fecha_venc_pago,
            v_reg.fecha_pago,
            v_reg.dias_morosidad,
            v_reg.especialidad,
            v_multa_total
        );
    END LOOP;

    CLOSE c_morosidad;
    DBMS_OUTPUT.PUT_LINE('Proceso de acreditación ejecutado para el año ' || v_anno_proceso);
END;
/
-- Verificar resultados
SELECT * FROM pago_moroso;



------------------------------------------------------------
-- CASO 2: Destinación de médicos según unidad y atenciones
------------------------------------------------------------
DECLARE
    -- Año actual y año de proceso (anterior)
    v_anno_actual  NUMBER := EXTRACT(YEAR FROM SYSDATE);
    v_anno_proceso NUMBER := v_anno_actual - 1;

    ------------------------------------------------------------
    -- Cursor: obtiene médicos y sus atenciones del año anterior
    ------------------------------------------------------------
    CURSOR c_medicos IS
        SELECT
            m.med_run,
            m.dv_run,
            m.pnombre || ' ' || m.snombre || ' ' || m.apaterno || ' ' || m.amaterno AS nombre_medico,
            u.uni_id,
            u.nombre AS unidad,
            COUNT(a.ate_id) AS total_atenciones,
            m.apaterno
        FROM medico m
             JOIN unidad u ON m.uni_id = u.uni_id
             LEFT JOIN atencion a ON m.med_run = a.med_run
                                   AND EXTRACT(YEAR FROM a.fecha_atencion) = v_anno_proceso
        GROUP BY m.med_run, m.dv_run, m.pnombre, m.snombre, m.apaterno, m.amaterno, u.uni_id, u.nombre
        ORDER BY unidad, m.apaterno;

    -- Registro del cursor
    v_reg c_medicos%ROWTYPE;

    ------------------------------------------------------------
    -- VARRAY de posibles destinaciones
    ------------------------------------------------------------
    TYPE t_destinaciones IS VARRAY(4) OF VARCHAR2(100);
    v_destinos t_destinaciones := t_destinaciones(
        'Servicio de Atención Primaria de Urgencia (SAPU)',
        'Centros de Salud Familiar (CESFAM)',
        'Consultorios Generales',
        'Hospitales del área de la Salud Pública'
    );

    -- Variables auxiliares
    v_destinacion VARCHAR2(100);
    v_correo      VARCHAR2(100);
BEGIN
    -- Limpiar tabla destino antes de cargar nuevos datos
    EXECUTE IMMEDIATE 'TRUNCATE TABLE medico_servicio_comunidad';

    OPEN c_medicos;
    LOOP
        FETCH c_medicos INTO v_reg;
        EXIT WHEN c_medicos%NOTFOUND;

        ------------------------------------------------------------
        -- Determinar destinación según unidad y número de atenciones
        ------------------------------------------------------------
        IF v_reg.uni_id IN (100, 400) THEN
            v_destinacion := v_destinos(1); -- SAPU

        ELSIF v_reg.uni_id = 200 THEN
            IF v_reg.total_atenciones <= 3 THEN
                v_destinacion := v_destinos(1); -- SAPU
            ELSE
                v_destinacion := v_destinos(4); -- Hospital
            END IF;

        ELSIF v_reg.uni_id IN (500, 900) THEN
            v_destinacion := v_destinos(4); -- Hospital

        ELSIF v_reg.uni_id IN (700, 800) THEN
            IF v_reg.total_atenciones <= 3 THEN
                v_destinacion := v_destinos(1); -- SAPU
            ELSE
                v_destinacion := v_destinos(4); -- Hospital
            END IF;

        ELSIF v_reg.uni_id = 300 THEN
            v_destinacion := v_destinos(4); -- Hospital

        ELSIF v_reg.uni_id = 600 THEN
            v_destinacion := v_destinos(2); -- CESFAM

        ELSIF v_reg.uni_id = 1000 THEN
            IF v_reg.total_atenciones <= 3 THEN
                v_destinacion := v_destinos(1); -- SAPU
            ELSE
                v_destinacion := v_destinos(4); -- Hospital
            END IF;

        ELSE
            v_destinacion := v_destinos(3); -- Consultorio General
        END IF;

        ------------------------------------------------------------
        -- Generar correo institucional
        -- Formato: primeras 2 letras de unidad (mayúsculas)
        -- + últimas 2 letras del apellido (minúsculas)
        -- + últimos 3 dígitos del RUN
        -- + dominio institucional
        ------------------------------------------------------------
        v_correo := UPPER(SUBSTR(v_reg.unidad, 1, 2))
                    || LOWER(SUBSTR(v_reg.apaterno, LENGTH(v_reg.apaterno) - 2, 2))
                    || SUBSTR(v_reg.med_run, LENGTH(v_reg.med_run) - 2, 3)
                    || '@medicocktk.cl';

        ------------------------------------------------------------
        -- Insertar registro en tabla destino MEDICO_SERVICIO_COMUNIDAD
        ------------------------------------------------------------
        INSERT INTO medico_servicio_comunidad (
            unidad,
            run_medico,
            nombre_medico,
            correo_institucional,
            total_aten_medicas,
            destinacion
        ) VALUES (
            v_reg.unidad,
            TO_CHAR(v_reg.med_run, 'FM999G999G999') || '-' || v_reg.dv_run,
            v_reg.nombre_medico,
            v_correo,
            v_reg.total_atenciones,
            v_destinacion
        );
    END LOOP;

    CLOSE c_medicos;
    DBMS_OUTPUT.PUT_LINE('Proceso ejecutado para el año ' || v_anno_proceso);
END;
/

