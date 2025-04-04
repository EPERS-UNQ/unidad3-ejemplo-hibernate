-- Ejecutar después de la primera consulta en Terminal 1 pero antes del INSERT
BEGIN;
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Calcular suma actual de vida
SELECT SUM(vida) AS total_vida FROM personaje;
-- Mostrará 430

-- Basado en esto, decidimos que podemos nuestros heroes andan bajos de vida,
-- y necesitamos agregar un nuevo personaje para que los ayude
INSERT INTO personaje (nombre, pesomaximo,vida)
VALUES ('Gimli', 90, 140);

COMMIT;