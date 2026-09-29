package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.CatalogServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CatalogServiceIntegrationTest {

    @Autowired
    private CatalogServiceImpl catalogService;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private SessionDao sessionDao;

    private Movie testMovie;
    private Session testSession;

    @BeforeEach
    public void setUp() {
        testMovie = new Movie("Title Integration", "Summary", 120);
        movieDao.save(testMovie);

        Room testRoom = new Room("Room 1", 50);
        roomDao.save(testRoom);

        testSession = new Session(testMovie, testRoom, LocalDateTime.now().plusDays(1), new BigDecimal("5.00"));
        sessionDao.save(testSession);
    }

    @Test
    public void testFindNowPlayingMoviesReturnsExpectedSessionCount() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        List<Session> sessions = catalogService.findNowPlayingMovies(tomorrow);
        
        assertEquals(1, sessions.size());
    }

    @Test
    public void testFindMovieReturnsCorrectMovieId() throws Exception {
        Movie foundMovie = catalogService.findMovie(testMovie.getId());
        
        assertEquals(testMovie.getId(), foundMovie.getId());
    }

    @Test
    public void testFindMovieThrowsExceptionForNonExistentId() {
        assertThrows(InstanceNotFoundException.class, () -> catalogService.findMovie(-1L));
    }

    @Test
    public void testFindSessionReturnsCorrectSessionId() throws Exception {
        Session foundSession = catalogService.findSession(testSession.getId());
        
        assertEquals(testSession.getId(), foundSession.getId());
    }
}