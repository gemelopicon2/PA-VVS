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

-- 1. Insertar usuarios: "viewer" (rol 0), "ticketseller" (rol 1)
INSERT INTO User (userName, password, firstName, lastName, email, role) VALUES
('viewer',          '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Espectador', 'Pruebas',  'viewer@udc.es',          0),
('ticketseller',    '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Taquillero', 'Pruebas',  'seller@udc.es',          1),
('testviewer',      '$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Test',       'Viewer',   'testviewer@udc.es',      0),
('testticketseller','$2a$10$v.js2jCaX3xoKvkR6E2pbugMmZDBPlCAz2gA7EOIZhbkvsPFew/5u', 'Test',       'Seller',   'testticketseller@udc.es',1);
-- IDs resultantes: viewer=1, ticketseller=2, testviewer=3, testticketseller=4

-- 2. Insertar 2 salas
INSERT INTO Room (name, capacity) VALUES
('Sala 1 - Pequeña', 9),
('Sala 2 - Grande', 50);

-- 3. Insertar 2 películas
INSERT INTO Movie (title, summary, duration) VALUES
('Torrente, presidente', 'Torrente, presidente es la sexta parte de la saga de películas realizadas por el director Santiago Segura...', 166),
('El Padrino', 'La familia criminal Corleone...', 175);

-- 4. Insertar sesiones
INSERT INTO Session (movieId, roomId, date, price, availableTickets, version) VALUES
-- Hoy
(1, 2, DATE_ADD(DATE(NOW()), INTERVAL '0 00:05' DAY_MINUTE), 8.50, 50, 0),
(2, 1, DATE_ADD(DATE(NOW()), INTERVAL '0 23:55' DAY_MINUTE), 9.00,  9, 0),
-- Día 1
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '1 17:00' DAY_MINUTE), 8.50,  9, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '1 19:00' DAY_MINUTE), 9.00, 50, 0),
-- Día 2
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '2 17:00' DAY_MINUTE), 8.50,  9, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '2 19:00' DAY_MINUTE), 9.00, 50, 0),
-- Día 3
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '3 17:00' DAY_MINUTE), 8.50,  9, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '3 19:00' DAY_MINUTE), 9.00, 50, 0),
-- Día 4
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '4 17:00' DAY_MINUTE), 8.50,  9, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '4 19:00' DAY_MINUTE), 9.00, 50, 0),
-- Día 5
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '5 17:00' DAY_MINUTE), 8.50,  9, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '5 19:00' DAY_MINUTE), 9.00, 50, 0),
-- Día 6
(1, 1, DATE_ADD(DATE(NOW()), INTERVAL '6 17:00' DAY_MINUTE), 8.50,  9, 0),
(2, 2, DATE_ADD(DATE(NOW()), INTERVAL '6 19:00' DAY_MINUTE), 9.00, 50, 0),
-- E2E: sesión para mañana a las 01:00 con entradas libres (id=15)
(1, 2, DATE_ADD(DATE(NOW()), INTERVAL '1 01:00' DAY_MINUTE), 8.50, 50, 0);

-- 5. Insertar compras
-- Las dos compras originales de "viewer" (id=1) para sesión id=1
INSERT INTO Purchase (userId, sessionId, tickets, creditCard, date, delivered) VALUES
(1, 1, 2, '1234567890123456', ADDDATE(NOW(), INTERVAL -2 DAY), 0),
(1, 1, 3, '9876543210987654', ADDDATE(NOW(), INTERVAL -1 DAY), 0),
-- E2E: compra de "testviewer" (id=3) para la sesión de mañana a las 01:00 (id=15)
(3, 15, 2, '1111222233334444', ADDDATE(NOW(), INTERVAL -1 DAY), 0);
-- IDs resultantes: compra E2E = id 3