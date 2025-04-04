-- Ejecutar después de la primera lectura en Terminal 1
BEGIN;
--SET TRANSACTION ISOLATION LEVEL READ COMMITTED;  No hace falta ser declarativo con el isolation level. Esto es el default.

-- Aghhh, Frodo fue atacado por un Nazgul!! Vamos modificar la vida de Frodo
-- e insertar a Gimli quien ya esta esperando en el consejo de Elrond
UPDATE personaje SET vida = 30 WHERE nombre = 'Frodo';

INSERT INTO personaje (nombre, pesomaximo, vida) VALUES
    ('Gimli', 200, 250);

-- Confirmar el cambio inmediatamente
COMMIT;

-- Verificar los cambios
SELECT * FROM personaje;