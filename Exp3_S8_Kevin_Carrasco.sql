--Caso 1
--------------------------------------------------------
-- TRIGGER para mantener TOTAL_CONSUMOS sincronizado
--------------------------------------------------------
CREATE OR REPLACE TRIGGER trg_consumo_total
AFTER INSERT OR UPDATE OR DELETE ON consumo
FOR EACH ROW
BEGIN
   -- Caso INSERT
   IF INSERTING THEN
      MERGE INTO total_consumos t
      USING (SELECT :NEW.id_huesped AS id_huesped, :NEW.monto AS monto FROM dual) s
      ON (t.id_huesped = s.id_huesped)
      WHEN MATCHED THEN
         UPDATE SET t.monto_consumos = t.monto_consumos + s.monto
      WHEN NOT MATCHED THEN
         INSERT (id_huesped, monto_consumos) VALUES (s.id_huesped, s.monto);

   -- Caso UPDATE
   ELSIF UPDATING THEN
      UPDATE total_consumos
      SET monto_consumos = monto_consumos - :OLD.monto + :NEW.monto
      WHERE id_huesped = :OLD.id_huesped;

   -- Caso DELETE
   ELSIF DELETING THEN
      UPDATE total_consumos
      SET monto_consumos = monto_consumos - :OLD.monto
      WHERE id_huesped = :OLD.id_huesped;
   END IF;
END;
/
--------------------------------------------------------
-- TRIGGER para asignar id_consumo con MAX+1
--------------------------------------------------------
CREATE OR REPLACE TRIGGER trg_consumo_id
BEFORE INSERT ON consumo
FOR EACH ROW
DECLARE
   v_next_id NUMBER;
BEGIN
   IF :NEW.id_consumo IS NULL THEN
      SELECT NVL(MAX(id_consumo),0)+1
      INTO v_next_id
      FROM consumo;

      :NEW.id_consumo := v_next_id;
   END IF;
END;
/
--------------------------------------------------------
-- BLOQUE DE PRUEBAS
--------------------------------------------------------
DECLARE
BEGIN
   -- a) Insertar nuevo consumo (id se asigna automáticamente con MAX+1)
   INSERT INTO consumo (id_reserva, id_huesped, monto)
   VALUES (1587, 340006, 150);

   -- b) Eliminar consumo con ID 11473
   DELETE FROM consumo WHERE id_consumo = 11473;

   -- c) Actualizar monto del consumo con ID 10688
   UPDATE consumo
   SET monto = 95
   WHERE id_consumo = 10688;

   COMMIT;
END;
/


SELECT * FROM TOTAL_CONSUMOS
WHERE id_huesped IN (340003, 340004, 340006, 340008, 340009)
ORDER BY ID_HUESPED;


SELECT *
FROM consumo
WHERE id_huesped IN (340003, 340004, 340006, 340008, 340009)
order by id_consumo;

--Caso 2
--------------------------------------------------------
-- PACKAGE para cálculo de tours
--------------------------------------------------------
CREATE OR REPLACE PACKAGE pkg_tours IS
   v_monto_tours NUMBER := 0; -- variable opcional
   FUNCTION fn_monto_tours(p_id_huesped NUMBER) RETURN NUMBER;
END pkg_tours;
/

CREATE OR REPLACE PACKAGE BODY pkg_tours IS
   FUNCTION fn_monto_tours(p_id_huesped NUMBER) RETURN NUMBER IS
      v_total NUMBER := 0;
   BEGIN
      SELECT NVL(SUM(t.valor_tour),0)
      INTO v_total
      FROM huesped_tour ht
      JOIN tour t ON ht.id_tour = t.id_tour
      WHERE ht.id_huesped = p_id_huesped;

      v_monto_tours := v_total;
      RETURN v_total;
   EXCEPTION
      WHEN OTHERS THEN
         RETURN 0;
   END fn_monto_tours;
END pkg_tours;
/

--------------------------------------------------------
-- FUNCIÓN: Agencia del huésped con manejo de errores
--------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_agencia_huesped(p_id_huesped NUMBER)
RETURN VARCHAR2
IS
   v_agencia   VARCHAR2(100);
   v_msg_error VARCHAR2(4000);
BEGIN
   SELECT a.nom_agencia
   INTO v_agencia
   FROM huesped h
   JOIN agencia a ON h.id_agencia = a.id_agencia
   WHERE h.id_huesped = p_id_huesped;

   RETURN v_agencia;

EXCEPTION
   WHEN NO_DATA_FOUND THEN
      v_msg_error := SQLERRM;

      INSERT INTO reg_errores (id_error, nomsubprograma, msg_error)
      VALUES (
         sq_error.NEXTVAL,
         'FN_AGENCIA',
         'Error en la función FN_AGENCIA al recuperar la agencia del huésped con id '
         || p_id_huesped || ' - ' || v_msg_error
      );

      RETURN 'NO REGISTRA AGENCIA';

   WHEN OTHERS THEN
      v_msg_error := SQLERRM;

      INSERT INTO reg_errores (id_error, nomsubprograma, msg_error)
      VALUES (
         sq_error.NEXTVAL,
         'FN_AGENCIA',
         v_msg_error
      );

      RETURN 'NO REGISTRA AGENCIA';
END;
/

--------------------------------------------------------
-- FUNCIÓN: Consumos del huésped
--------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_consumos_huesped(p_id_huesped NUMBER)
RETURN NUMBER
IS
   v_consumos  NUMBER;
   v_msg_error VARCHAR2(4000);
BEGIN
   SELECT monto_consumos
   INTO v_consumos
   FROM total_consumos
   WHERE id_huesped = p_id_huesped;

   RETURN NVL(v_consumos, 0);

EXCEPTION
   WHEN NO_DATA_FOUND THEN
      v_msg_error := SQLERRM;

      INSERT INTO reg_errores (id_error, nomsubprograma, msg_error)
      VALUES (
         sq_error.NEXTVAL,
         'FN_CONSUMOS',
         'Error en la función FN_CONSUMOS al recuperar los consumos del cliente con id '
         || p_id_huesped || ' - ' || v_msg_error
      );

      RETURN 0;

   WHEN OTHERS THEN
      v_msg_error := SQLERRM;

      INSERT INTO reg_errores (id_error, nomsubprograma, msg_error)
      VALUES (
         sq_error.NEXTVAL,
         'FN_CONSUMOS',
         v_msg_error
      );

      RETURN 0;
END;
/

--------------------------------------------------------
-- PROCEDIMIENTO PRINCIPAL: cálculo de pagos
--------------------------------------------------------
CREATE OR REPLACE PROCEDURE pr_calculo_pagos(p_fecha DATE, p_valor_dolar NUMBER) IS

   CURSOR c_huespedes IS
      SELECT
         r.id_huesped,
         r.id_reserva,
         r.estadia,
         h.nom_huesped,
         h.id_agencia
      FROM reserva r
      JOIN huesped h
        ON r.id_huesped = h.id_huesped
      WHERE TRUNC(r.ingreso + r.estadia) = TRUNC(p_fecha);

   v_valor_habitacion NUMBER := 0;
   v_valor_minibar    NUMBER := 0;
   v_consumos         NUMBER := 0;
   v_tours            NUMBER := 0;
   v_agencia          VARCHAR2(100);
   v_subtotal         NUMBER := 0;
   v_desc_agencia     NUMBER := 0;
   v_desc_tramo       NUMBER := 0;
   v_total            NUMBER := 0;

   c_valor_persona_pesos CONSTANT NUMBER := 35000; -- regla de negocio

BEGIN
   -- Limpiar resultados y errores
   EXECUTE IMMEDIATE 'TRUNCATE TABLE detalle_diario_huespedes';
   EXECUTE IMMEDIATE 'TRUNCATE TABLE reg_errores';

   FOR rec IN c_huespedes LOOP

      -- Alojamiento diario (habitación + minibar)
      SELECT NVL(SUM(hab.valor_habitacion), 0),
             NVL(SUM(hab.valor_minibar), 0)
        INTO v_valor_habitacion, v_valor_minibar
        FROM habitacion hab
        JOIN detalle_reserva dr
          ON hab.id_habitacion = dr.id_habitacion
       WHERE dr.id_reserva = rec.id_reserva;

      -- Consumos
      v_consumos := fn_consumos_huesped(rec.id_huesped);
      --Tours para no dejar la fila de la tabla en 0(No se suma en los totales)
      v_tours    := pkg_tours.fn_monto_tours(rec.id_huesped);

      -- Nombre agencia (solo para mostrar)
      v_agencia := fn_agencia_huesped(rec.id_huesped);

      -- Subtotal (NO incluye tours)
      v_subtotal :=
            ((v_valor_habitacion + v_valor_minibar) * rec.estadia)
          + v_consumos
          + (c_valor_persona_pesos / p_valor_dolar);

      -- Descuento por agencia
      IF rec.id_agencia = 4 THEN
         v_desc_agencia := v_subtotal * 0.12;
      ELSE
         v_desc_agencia := 0;
      END IF;

      -- Descuento por tramo de consumo
      DECLARE
         v_pct NUMBER := 0;
      BEGIN
         SELECT NVL(MAX(pct),0)
         INTO v_pct
         FROM tramos_consumos
         WHERE v_consumos BETWEEN vmin_tramo AND vmax_tramo;

         v_desc_tramo := v_subtotal * v_pct;
      END;

      -- Total = subtotal - descuentos acumulados (Sin Tours)
      v_total := v_subtotal - (v_desc_agencia + v_desc_tramo);

      -- Insertar en pesos
      INSERT INTO detalle_diario_huespedes (
         id_huesped,
         nombre,
         agencia,
         alojamiento,
         consumos,
         tours,
         subtotal_pago,
         descuento_consumos,
         descuentos_agencia,
         total
      ) VALUES (
         rec.id_huesped,
         rec.nom_huesped,
         v_agencia,
         ROUND(((v_valor_habitacion + v_valor_minibar) * rec.estadia) * p_valor_dolar),
         ROUND(v_consumos * p_valor_dolar),
         ROUND(v_tours * p_valor_dolar),
         ROUND(v_subtotal * p_valor_dolar),
         ROUND(v_desc_tramo * p_valor_dolar),
         ROUND(v_desc_agencia * p_valor_dolar),
         ROUND(v_total * p_valor_dolar)
      );

   END LOOP;
END pr_calculo_pagos;
/


--------------------------------------------------------
-- EJECUCIÓN DE PRUEBA
--------------------------------------------------------
BEGIN
   pr_calculo_pagos(DATE '2021-08-18', 915);
END;
/