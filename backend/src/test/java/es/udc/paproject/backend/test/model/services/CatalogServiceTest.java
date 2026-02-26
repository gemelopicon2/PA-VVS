package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.CatalogService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CatalogServiceTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private SessionDao sessionDao;

    /* Métodos auxiliares para crear datos de prueba */
    private Movie createMovie(String title) {
        return new Movie(title, "Summary for " + title, 120);
    }

    private Room createRoom(String name) {
        return new Room(name, 100);
    }

    @Test
    public void testFindSessionByNonExistentId() {
        assertThrows(InstanceNotFoundException.class, () -> catalogService.findSession(-1L));
    }

    @Test
    public void testFindSessionById() throws InstanceNotFoundException {

        // 1. Preparación de datos
        Movie movie = createMovie("Avatar");
        Room room = createRoom("Sala 1");

        movieDao.save(movie);
        roomDao.save(room);

        Session session = new Session(movie, room, LocalDateTime.now().plusHours(2), new BigDecimal(10));
        sessionDao.save(session);

        // 2. Ejecución
        Session foundSession = catalogService.findSession(session.getId());

        // 3. Comprobación
        assertEquals(session, foundSession);
        assertEquals(movie, foundSession.getMovie());
        assertEquals(room, foundSession.getRoom());
    }
}