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
-- ID 1: Una sesión en el PASADO (ya comenzada) para "Torrente, presidente" en "Sala 1".
-- ID 2: Una sesión en el FUTURO (aún no comenzada) para "El Padrino" en "Sala 2".
INSERT INTO Session (movieId, roomId, date, price) VALUES
(1, 1, ADDDATE(NOW(), INTERVAL -1 DAY), 8.50),
(2, 2, ADDDATE(NOW(), INTERVAL 5 DAY), 9.00);

-- 5. Insertar 1 compra:
-- El usuario "viewer" compró 2 entradas para la sesión ID 1 (que ya empezó).
INSERT INTO Purchase (userId, sessionId, tickets, creditCard, date, delivered) VALUES
(1, 1, 2, '1234567890123456', ADDDATE(NOW(), INTERVAL -2 DAY), 0);