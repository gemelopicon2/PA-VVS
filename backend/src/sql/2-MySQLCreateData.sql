-- ----------------------------------------------------------------------------
-- Put here INSERT statements for inserting data required by the application
-- in the "paproject" database.
-------------------------------------------------------------------------------

-- Borrado previo de datos por si se ejecuta varias veces
DELETE FROM Purchase;
DELETE FROM Session;
DELETE FROM Room;
DELETE FROM Movie;
DELETE FROM User;

-- Reiniciar los IDs automáticos a 1
ALTER TABLE Purchase AUTO_INCREMENT = 1;
ALTER TABLE Session AUTO_INCREMENT = 1;
ALTER TABLE Room AUTO_INCREMENT = 1;
ALTER TABLE Movie AUTO_INCREMENT = 1;
ALTER TABLE User AUTO_INCREMENT = 1;

-- 1. Insertar 2 usuarios: "viewer" (rol 0) y "ticketseller" (rol 1)
-- Ambos con la contraseña "pa2526" cifrada.
INSERT INTO User (userName, password, firstName, lastName, email, role) VALUES
('viewer', '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Espectador', 'Pruebas', 'viewer@udc.es', 0),
('ticketseller', '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Taquillero', 'Pruebas', 'seller@udc.es', 1);

-- 2. Insertar 2 salas
INSERT INTO Room (name, capacity) VALUES
('Sala 1 - Pequeña', 9),
('Sala 2 - Grande', 50);

-- 3. Insertar 2 películas
INSERT INTO Movie (title, summary, duration) VALUES
('Torrente, presidente', 'Torrente, presidente es la sexta parte de la saga de películas realizadas por el director Santiago Segura...', 166),
('El Padrino', 'La familia criminal Corleone...', 175);

-- 4. Insertar 2 sesiones:
-- ID 1: Una sesión en el PASADO (ya comenzada) para "Torrente, presidente" en "Sala 2".
-- ID 2: Una sesión en el FUTURO (aún no comenzada) para "El Padrino" en "Sala 1".
INSERT INTO Session (movieId, roomId, date, price) VALUES
(1, 2, DATE_ADD(DATE(NOW()), INTERVAL '0 00:05' DAY_MINUTE), 8.50),
-- Sesión 2: hoy 23:55 (sala con 9 localidades)
(2, 1, DATE_ADD(DATE(NOW()), INTERVAL '0 23:55' DAY_MINUTE), 9.00),
-- 2 sesiones por cada uno de los 6 días siguientes
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '1 17:00' DAY_MINUTE), 8.50),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '1 19:00' DAY_MINUTE), 9.00),
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '2 17:00' DAY_MINUTE), 8.50),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '2 19:00' DAY_MINUTE), 9.00),
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '3 17:00' DAY_MINUTE), 8.50),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '3 19:00' DAY_MINUTE), 9.00),
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '4 17:00' DAY_MINUTE), 8.50),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '4 19:00' DAY_MINUTE), 9.00),
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '5 17:00' DAY_MINUTE), 8.50),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '5 19:00' DAY_MINUTE), 9.00),
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '6 17:00' DAY_MINUTE), 8.50),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '6 19:00' DAY_MINUTE), 9.00);

-- 5. Insertar 2 compras:
-- El usuario "viewer" compró 2 entradas para la sesión ID 1 (que ya empezó).
INSERT INTO Purchase (userId, sessionId, tickets, creditCard, date, delivered) VALUES
(1, 1, 2, '1234567890123456', ADDDATE(NOW(), INTERVAL -2 DAY), 0),
(1, 1, 3, '9876543210987654', ADDDATE(NOW(), INTERVAL -1 DAY), 0);