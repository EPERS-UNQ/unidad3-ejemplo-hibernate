-- https://www.postgresql.org/docs/current/sql-set-transaction.html

-- Consistencia de lectura: Si estás realizando múltiples consultas relacionadas dentro de una transacción (por ejemplo, generando un informe complejo), necesitas que todas las lecturas sean consistentes entre sí, como una "foto" del momento en que comenzó la transacción.
-- Protección contra cambios concurrentes: REPEATABLE READ no permite que dos transacciones modifiquen los mismos datos concurrentemente. Si una transacción modifica datos que otra transacción había leído, una de ellas fallará con un error de serialización.


-- Asegurarse de que tenemos datos iniciales
TRUNCATE personaje RESTART IDENTITY CASCADE;
INSERT INTO personaje (nombre, pesomaximo, xp, vida) VALUES
                                                         ('Frodo', 50, 100, 80),
                                                         ('Gandalf', 70, 1000, 200),
                                                         ('Aragorn', 100, 800, 150);

-- Iniciar transacción con REPEATABLE READ
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Leer datos iniciales de Frodo
SELECT * FROM personaje WHERE nombre = 'Frodo';

-- Actualizar la vida de Frodo
UPDATE personaje SET vida = 40 WHERE nombre = 'Frodo';

-- Confirmar los cambios
COMMIT;

-- Verificar que el cambio se ha confirmado
SELECT * FROM personaje WHERE nombre = 'Frodo';