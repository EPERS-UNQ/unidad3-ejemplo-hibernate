-- https://www.postgresql.org/docs/current/sql-set-transaction.html

TRUNCATE personaje RESTART IDENTITY CASCADE;

-- En este ejemplo vamos a poder ver como se comporta una transaccion con nivel de aislamiento READ COMMITED
-- Y el fenomeno de Lectura no repetida y lectura fantasma.

-- Arrancamos con un set de datos de prueba
INSERT INTO personaje (nombre, pesomaximo, xp, vida) VALUES
                                                         ('Frodo', 50, 100, 80),
                                                         ('Gandalf', 70, 1000, 200),
                                                         ('Aragorn', 100, 800, 150);

-- Iniciar transacción con REPEATABLE READ
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Primera lectura de tabla personajes, no cambio nada
SELECT * FROM personaje;

-- Ahora nos vamos a la segunda transaccion y la finalizamos.

-- Segunda lectura de tabla personajes (podremos ver los cambios confirmado por Terminal 2, pero solo a Gimli, Frodo esta igual)
SELECT * FROM personaje;

-- Finalizar transacción
COMMIT;