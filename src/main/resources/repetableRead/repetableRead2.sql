-- Ejecutar después de la primera consulta en Terminal 1 pero antes del INSERT
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Decidimos que vamos a agregar a Gimli hijo de Gloin
INSERT INTO personaje (nombre, pesomaximo, vida)
VALUES ('Gimli', 90, 140);

COMMIT;