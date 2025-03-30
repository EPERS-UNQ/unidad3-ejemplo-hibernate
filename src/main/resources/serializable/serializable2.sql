-- Ejecutar después de la primera consulta en Terminal 1 pero antes del INSERT
BEGIN;
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Calcular suma actual de XP
SELECT SUM(xp) AS total_xp FROM personaje;
-- Mostrará 1900

-- Basado en esto, decidimos que podemos agregar un personaje con 900 XP
-- (ya que 1900 + 900 < 3000)
INSERT INTO personaje (nombre, pesomaximo, xp, vida)
VALUES ('Gimli', 90, 900, 140);

COMMIT;