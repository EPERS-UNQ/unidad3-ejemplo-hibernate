-- Reiniciar datos
TRUNCATE personaje RESTART IDENTITY CASCADE;
INSERT INTO personaje (nombre, pesomaximo, xp, vida) VALUES
                                                         ('Frodo', 50, 100, 80),
                                                         ('Gandalf', 70, 1000, 200),
                                                         ('Aragorn', 100, 800, 150);

-- Iniciar transacción con REPEATABLE READ
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Calcular suma actual de XP
SELECT SUM(xp) AS total_xp FROM personaje;
-- Mostrará 1900

-- Basado en esto, decidimos que podemos agregar un personaje con 1000 XP
-- (ya que 1900 + 1000 < 3000)
SELECT pg_sleep(15); -- Pausa para que T2 ejecute su consulta

INSERT INTO personaje (nombre, pesomaximo, xp, vida)
VALUES ('Legolas', 80, 1000, 120);

COMMIT;