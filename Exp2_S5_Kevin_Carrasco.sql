SET SERVEROUTPUT ON;
-- =========================
--   VARIABLE BIND DEL AÑO
-- =========================
VARIABLE b_anio NUMBER;
EXEC :b_anio := EXTRACT(YEAR FROM SYSDATE);

DECLARE
  /* -------------------------------------------
      PERIODO DE EJECUCIÓN PARAMÉTRICO
     ------------------------------------------- */
  v_anio_proceso NUMBER(4) := :b_anio;

  /* --------------------------------------
      VARRAY CON TIPOS DE TRANSACCIÓN
     ------------------------------------- */
  TYPE t_tipos_transaccion IS VARRAY(2) OF NUMBER;
  v_tipos_transaccion t_tipos_transaccion := t_tipos_transaccion(102, 103);

  /* -----------------------
      REGISTRO PL/SQL
     ----------------------- */
  TYPE r_detalle IS RECORD (
    numrun            cliente.numrun%TYPE,
    dvrun             cliente.dvrun%TYPE,
    nro_tarjeta       tarjeta_cliente.nro_tarjeta%TYPE,
    nro_transaccion   transaccion_tarjeta_cliente.nro_transaccion%TYPE,
    fecha_transaccion transaccion_tarjeta_cliente.fecha_transaccion%TYPE,
    cod_tptran        transaccion_tarjeta_cliente.cod_tptran_tarjeta%TYPE,
    monto_total       transaccion_tarjeta_cliente.monto_total_transaccion%TYPE
  );
  v_reg r_detalle;

  /* ----------------------------
         CURSORES EXPLÍCITOS
     ---------------------------- */
  CURSOR c_grupos IS
    SELECT TO_CHAR(tr.fecha_transaccion, 'MMYYYY') AS mes_anno,
           tr.cod_tptran_tarjeta
    FROM   transaccion_tarjeta_cliente tr
    WHERE  EXTRACT(YEAR FROM tr.fecha_transaccion) = v_anio_proceso
    AND    tr.cod_tptran_tarjeta IN (102, 103)
    GROUP BY TO_CHAR(tr.fecha_transaccion, 'MMYYYY'),
             tr.cod_tptran_tarjeta
    ORDER BY TO_CHAR(tr.fecha_transaccion, 'MMYYYY'),
             tr.cod_tptran_tarjeta;

  CURSOR c_detalle(p_mes_anno VARCHAR2, p_cod_tptran NUMBER) IS
    SELECT c.numrun,
           c.dvrun,
           t.nro_tarjeta,
           tr.nro_transaccion,
           tr.fecha_transaccion,
           tr.cod_tptran_tarjeta,
           tr.monto_total_transaccion
    FROM   cliente c
           JOIN tarjeta_cliente t
             ON c.numrun = t.numrun
           JOIN transaccion_tarjeta_cliente tr
             ON t.nro_tarjeta = tr.nro_tarjeta
    WHERE  EXTRACT(YEAR FROM tr.fecha_transaccion) = v_anio_proceso
    AND    TO_CHAR(tr.fecha_transaccion, 'MMYYYY') = p_mes_anno
    AND    tr.cod_tptran_tarjeta = p_cod_tptran
    ORDER BY tr.fecha_transaccion, c.numrun;

  /* ----------------------------
         VARIABLES DE CÁLCULO
     ---------------------------- */
  v_porcentaje_aporte tramo_aporte_sbif.porc_aporte_sbif%TYPE;
  v_aporte_calculado  NUMBER(12);
  v_sum_monto_total   NUMBER(12);
  v_sum_aporte_total  NUMBER(12);

  /* ----------------------------
         CONTADORES DE CONTROL
     ---------------------------- */
  v_total_registros      NUMBER := 0;
  v_registros_procesados NUMBER := 0;

  /* ----------------------------
         EXCEPCIÓN DEFINIDA POR EL USUARIO
     ---------------------------- */
  e_proceso_incompleto EXCEPTION;

BEGIN
  /* ----------------------------
         TRUNCATE EN TIEMPO DE EJECUCIÓN
     ---------------------------- */
  EXECUTE IMMEDIATE 'TRUNCATE TABLE detalle_aporte_sbif';
  EXECUTE IMMEDIATE 'TRUNCATE TABLE resumen_aporte_sbif';

  /* Total de registros esperados */
  SELECT COUNT(*)
    INTO v_total_registros
    FROM transaccion_tarjeta_cliente
   WHERE EXTRACT(YEAR FROM fecha_transaccion) = v_anio_proceso
     AND cod_tptran_tarjeta IN (102, 103);

  /* ----------------------------
         PROCESO PRINCIPAL
     ---------------------------- */
  FOR g IN c_grupos LOOP
    v_sum_monto_total  := 0;
    v_sum_aporte_total := 0;

    FOR d IN c_detalle(g.mes_anno, g.cod_tptran_tarjeta) LOOP
      /* Uso del REGISTRO PL/SQL */
      v_reg.numrun            := d.numrun;
      v_reg.dvrun             := d.dvrun;
      v_reg.nro_tarjeta       := d.nro_tarjeta;
      v_reg.nro_transaccion   := d.nro_transaccion;
      v_reg.fecha_transaccion := d.fecha_transaccion;
      v_reg.cod_tptran        := d.cod_tptran_tarjeta;
      v_reg.monto_total       := d.monto_total_transaccion;

      /* Bloque anidado con excepciones */
      BEGIN
        SELECT porc_aporte_sbif
          INTO v_porcentaje_aporte
          FROM tramo_aporte_sbif
         WHERE v_reg.monto_total BETWEEN tramo_inf_av_sav AND tramo_sup_av_sav;

        /* Cálculo en PL/SQL */
        v_aporte_calculado := ROUND(v_reg.monto_total * (v_porcentaje_aporte / 100));

        /* Inserción detalle */
        INSERT INTO detalle_aporte_sbif
        VALUES (
          v_reg.numrun,
          v_reg.dvrun,
          v_reg.nro_tarjeta,
          v_reg.nro_transaccion,
          v_reg.fecha_transaccion,
          CASE v_reg.cod_tptran
            WHEN 102 THEN 'Avance en Efectivo'
            WHEN 103 THEN 'Súper Avance en Efectivo'
          END,
          v_reg.monto_total,
          v_aporte_calculado
        );

        v_sum_monto_total  := v_sum_monto_total  + v_reg.monto_total;
        v_sum_aporte_total := v_sum_aporte_total + v_aporte_calculado;
        v_registros_procesados := v_registros_procesados + 1;

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          DBMS_OUTPUT.PUT_LINE('Registro '||v_reg.nro_transaccion||' sin tramo SBIF. Se omite.');
        WHEN TOO_MANY_ROWS THEN
          DBMS_OUTPUT.PUT_LINE('Registro '||v_reg.nro_transaccion||' con tramos solapados. Se omite.');
        WHEN OTHERS THEN
          DBMS_OUTPUT.PUT_LINE('Error inesperado en registro '||v_reg.nro_transaccion||'. Se omite.');
      END;
    END LOOP;

    /* Inserción resumen */
    INSERT INTO resumen_aporte_sbif
    VALUES (
      g.mes_anno,
      CASE g.cod_tptran_tarjeta
        WHEN 102 THEN 'Avance en Efectivo'
        WHEN 103 THEN 'Súper Avance en Efectivo'
      END,
      v_sum_monto_total,
      v_sum_aporte_total
    );
  END LOOP;

  /* ----------------------------
         COMMIT CONTROLADO
     ---------------------------- */
  IF v_registros_procesados = v_total_registros THEN
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Proceso finalizado correctamente.');
    DBMS_OUTPUT.PUT_LINE('Total de registros procesados: ' || v_registros_procesados);
  ELSE
    RAISE e_proceso_incompleto;
  END IF;

EXCEPTION
  WHEN e_proceso_incompleto THEN
    ROLLBACK;
    DBMS_OUTPUT.PUT_LINE('Proceso incompleto. Rollback ejecutado.');
END;
/

