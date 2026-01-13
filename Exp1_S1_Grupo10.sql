
-- ============================
-- VARIABLES BIND CASO 1
-- ============================
VAR b_run_cliente           VARCHAR2(15);
VAR b_tramo1_max            NUMBER;
VAR b_tramo2_max            NUMBER;
VAR b_peso_normal_100k      NUMBER;
VAR b_extra_t1_100k         NUMBER;
VAR b_extra_t2_100k         NUMBER;
VAR b_extra_t3_100k         NUMBER;

-- ============================
-- VARIABLES BIND CASO 2
-- ============================
VAR b_nro_cliente_c2        NUMBER;
VAR b_nro_solic_credito_c2  NUMBER;
VAR b_cant_postergar_c2     NUMBER;

DECLARE
  -- Variables generales
  v_anio_anterior NUMBER := TO_NUMBER(TO_CHAR(ADD_MONTHS(SYSDATE, -12),'YYYY'));

  -- CASO 1
  v_nro_cliente    CLIENTE.NRO_CLIENTE%TYPE;
  v_nombre_cliente VARCHAR2(100);
  v_tipo_cliente   VARCHAR2(30);
  v_monto_anual    NUMBER := 0;
  v_factor_100k    NUMBER := 0;
  v_pesos_total    NUMBER := 0;

  -- CASO 2
  v_nombre_credito CREDITO.NOMBRE_CREDITO%TYPE;
  v_tasa           NUMBER := 0;
  v_ult_cuota      NUMBER;
  v_ult_fecha      DATE;
  v_ult_valor      NUMBER;
  v_total_creditos NUMBER;

BEGIN
  /* ============================================================
     CASO 1 – TODOSUMA
     ============================================================ */

  SELECT c.nro_cliente,
         TRIM(c.pnombre || ' ' || NVL(c.snombre,'') || ' ' ||
              c.appaterno || ' ' || NVL(c.apmaterno,'')),
         tc.nombre_tipo_cliente
    INTO v_nro_cliente, v_nombre_cliente, v_tipo_cliente
    FROM cliente c
    JOIN tipo_cliente tc ON tc.cod_tipo_cliente = c.cod_tipo_cliente
   WHERE (TO_CHAR(c.numrun) || '-' || c.dvrun) = :b_run_cliente;

  SELECT NVL(SUM(monto_solicitado),0)
    INTO v_monto_anual
    FROM credito_cliente
   WHERE nro_cliente = v_nro_cliente
     AND TO_CHAR(fecha_otorga_cred,'YYYY') = v_anio_anterior;

  v_factor_100k := TRUNC(v_monto_anual / 100000);
  v_pesos_total := v_factor_100k * :b_peso_normal_100k;

  IF UPPER(v_tipo_cliente) = 'TRABAJADORES INDEPENDIENTES' THEN
    IF v_monto_anual < :b_tramo1_max THEN
      v_pesos_total := v_pesos_total + v_factor_100k * :b_extra_t1_100k;
    ELSIF v_monto_anual <= :b_tramo2_max THEN
      v_pesos_total := v_pesos_total + v_factor_100k * :b_extra_t2_100k;
    ELSE
      v_pesos_total := v_pesos_total + v_factor_100k * :b_extra_t3_100k;
    END IF;
  END IF;

  INSERT INTO cliente_todosuma
  VALUES (v_nro_cliente, :b_run_cliente, v_nombre_cliente,
          v_tipo_cliente, v_monto_anual, v_pesos_total);

  /* ============================================================
     CASO 2 – POSTERGACIÓN DE CUOTAS
     ============================================================ */

  SELECT cr.nombre_credito
    INTO v_nombre_credito
    FROM credito_cliente cc
    JOIN credito cr ON cr.cod_credito = cc.cod_credito
   WHERE cc.nro_solic_credito = :b_nro_solic_credito_c2
     AND cc.nro_cliente = :b_nro_cliente_c2;

  IF v_nombre_credito = 'Crédito Hipotecario' THEN
    v_tasa := CASE :b_cant_postergar_c2 WHEN 1 THEN 0 ELSE 0.005 END;
  ELSIF v_nombre_credito = 'Crédito de Consumo' THEN
    v_tasa := 0.01;
  ELSIF v_nombre_credito = 'Crédito Automotriz' THEN
    v_tasa := 0.02;
  END IF;

  SELECT MAX(nro_cuota), MAX(fecha_venc_cuota), MAX(valor_cuota)
    INTO v_ult_cuota, v_ult_fecha, v_ult_valor
    FROM cuota_credito_cliente
   WHERE nro_solic_credito = :b_nro_solic_credito_c2;

  SELECT COUNT(*)
    INTO v_total_creditos
    FROM credito_cliente
   WHERE nro_cliente = :b_nro_cliente_c2
     AND TO_CHAR(fecha_otorga_cred,'YYYY') = v_anio_anterior;

  IF v_total_creditos > 1 THEN
    UPDATE cuota_credito_cliente
       SET fecha_pago_cuota = fecha_venc_cuota,
           monto_pagado = valor_cuota
     WHERE nro_solic_credito = :b_nro_solic_credito_c2
       AND nro_cuota = v_ult_cuota;
  END IF;

  FOR i IN 1 .. :b_cant_postergar_c2 LOOP
    INSERT INTO cuota_credito_cliente
    VALUES (:b_nro_solic_credito_c2,
            v_ult_cuota + i,
            ADD_MONTHS(v_ult_fecha,i),
            ROUND(v_ult_valor * (1 + v_tasa)),
            NULL,NULL,NULL,NULL);
  END LOOP;

  COMMIT;
END;
/
