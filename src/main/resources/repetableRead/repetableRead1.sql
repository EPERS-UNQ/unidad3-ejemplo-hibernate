-- https://www.postgresql.org/docs/current/sql-set-transaction.html
-- Repetable read da consistencia de lectura: Si estás realizando múltiples consultas relacionadas dentro de una transacción (por ejemplo, generando un informe complejo), necesitas que todas las lecturas sean consistentes entre sí, como una "foto" del momento en que comenzó la transacción.

TRUNCATE personaje RESTART IDENTITY CASCADE;

-- Arrancamos con un set de datos de prueba
INSERT INTO personaje (nombre, pesomaximo, vida) VALUES
                                                         ('Frodo', 50, 80),
                                                         ('Gandalf', 70, 200),
                                                         ('Aragorn', 100, 150);

-- Iniciar transacción con REPEATABLE READ
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Primera lectura de tabla personajes, no cambio nada
SELECT * FROM personaje;

-- Ahora nos vamos a la segunda transaccion y la finalizamos.

-- Segunda lectura de tabla personajes y saz!! son los mismos datos!
SELECT * FROM personaje;

-- Finalizar transacción
COMMIT;