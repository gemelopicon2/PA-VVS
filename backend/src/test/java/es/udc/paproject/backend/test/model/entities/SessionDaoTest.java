package es.udc.paproject.backend.test.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.MovieDao;
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.RoomDao;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SessionDaoTest {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private RoomDao roomDao;

    private Movie testMovie;
    private Room testRoom;

    @BeforeEach
    public void setUp() {
        // Se ejecuta antes de cada test para preparar las dependencias necesarias
        testMovie = new Movie("Test Movie", "summary", 120);
        movieDao.save(testMovie);

        testRoom = new Room("Sala 1", 50);
        roomDao.save(testRoom); // Solución al TransientPropertyValueException
    }

    @Test
    public void testFindByDateBetweenReturnsSessionInsideRange() {
        // Partición equivalente: Caso válido estándar
        LocalDateTime targetDate = LocalDateTime.of(2026, 10, 15, 18, 0);
        Session session = new Session(testMovie, testRoom, targetDate, new BigDecimal("7.50"));
        sessionDao.save(session);

        LocalDateTime startDate = LocalDateTime.of(2026, 10, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 10, 15, 23, 59);

        List<Session> foundSessions = sessionDao.findByDateBetween(startDate, endDate);

        assertEquals(1, foundSessions.size());
    }

    @Test
    public void testFindByDateBetweenExcludesSessionBeforeRange() {
        // Partición equivalente: Caso fuera de rango (anterior)
        LocalDateTime earlyDate = LocalDateTime.of(2026, 10, 14, 23, 59);
        Session session = new Session(testMovie, testRoom, earlyDate, new BigDecimal("7.50"));
        sessionDao.save(session);

        LocalDateTime startDate = LocalDateTime.of(2026, 10, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 10, 15, 23, 59);

        List<Session> foundSessions = sessionDao.findByDateBetween(startDate, endDate);

        assertTrue(foundSessions.isEmpty());
    }

    @Test
    public void testFindByDateBetweenExcludesSessionAfterRange() {
        // Partición equivalente: Caso fuera de rango (posterior)
        LocalDateTime lateDate = LocalDateTime.of(2026, 10, 16, 0, 0);
        Session session = new Session(testMovie, testRoom, lateDate, new BigDecimal("7.50"));
        sessionDao.save(session);

        LocalDateTime startDate = LocalDateTime.of(2026, 10, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 10, 15, 23, 59);

        List<Session> foundSessions = sessionDao.findByDateBetween(startDate, endDate);

        assertTrue(foundSessions.isEmpty());
    }

    @Test
    public void testFindByDateBetweenIncludesSessionOnExactStartDate() {
        // Valor frontera: Justo en el límite de inicio
        LocalDateTime startDate = LocalDateTime.of(2026, 10, 15, 0, 0);
        Session session = new Session(testMovie, testRoom, startDate, new BigDecimal("7.50"));
        sessionDao.save(session);

        LocalDateTime endDate = LocalDateTime.of(2026, 10, 15, 23, 59);

        List<Session> foundSessions = sessionDao.findByDateBetween(startDate, endDate);

        assertEquals(1, foundSessions.size());
    }
}