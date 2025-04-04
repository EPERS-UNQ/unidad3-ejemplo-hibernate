-- Reiniciar datos
TRUNCATE personaje RESTART IDENTITY CASCADE;
INSERT INTO personaje (nombre, pesomaximo, vida) VALUES
                                                         ('Frodo', 50, 80),
                                                         ('Gandalf', 70, 200),
                                                         ('Aragorn', 100, 150);

-- Iniciar transacción con SERIALIZABLE
BEGIN;
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Calcular suma actual de vida
SELECT SUM(vida) AS total_vida FROM personaje;
-- Mostrará 430

-- Basado en esto, decidimos que podemos nuestros heroes andan bajos de vida,
-- y necesitamos agregar un nuevo personaje para que los ayude
-- PERO ANTES DE QUE ESTO SUCEDA
-- Un usuario nuevo comenzo otra transaccion...

INSERT INTO personaje (nombre, pesomaximo, vida)
VALUES ('Legolas', 80, 120);

COMMIT;