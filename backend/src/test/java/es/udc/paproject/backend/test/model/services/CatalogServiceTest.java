package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.services.Block;
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

    private Movie createMovie(String title) {
        return new Movie(title, "Summary for " + title, 120);
    }

    private Room createRoom(String name) {
        return new Room(name, 100);
    }

    // -------------------------------------------------------------------------
    // Tests visualizar la cartelera (FUNC-1)
    // -------------------------------------------------------------------------

    @Test
    public void testFindNowPlayingMovies() throws InvalidDateException{
        Movie movieFuture = createMovie("Futura");
        Movie moviePast = createMovie("Pasada");
        Movie movieNoSessions = createMovie("Sin sesiones");
        movieDao.saveAll(List.of(movieFuture, moviePast, movieNoSessions));

        Room room = createRoom("Sala principal");
        roomDao.save(room);

        LocalDateTime now = LocalDateTime.now();
        Session futureSession = new Session(movieFuture, room, now.plusHours(2), BigDecimal.valueOf(10));
        Session pastSession = new Session(moviePast, room, now.minusHours(2), BigDecimal.valueOf(10));
        sessionDao.saveAll(List.of(futureSession, pastSession));

        Block<Movie> block = catalogService.findNowPlayingMovies(LocalDate.now(),0, 10);

        assertNotNull(block.getItems());
        assertEquals(1, block.getItems().size());
        assertEquals(movieFuture.getId(), block.getItems().get(0).getId());
        assertFalse(block.getExistMoreItems());
    }

    @Test
    public void testFindNowPlayingMovies_DistinctSession() throws InvalidDateException {
        Movie movie = createMovie("Película");
        movieDao.save(movie);
        Room room = createRoom("Sala");
        roomDao.save(room);
        LocalDateTime now = LocalDateTime.now();

        Session session1 = new Session(movie, room, now.plusHours(1), BigDecimal.valueOf(10));
        Session session2 = new Session(movie, room, now.plusHours(2), BigDecimal.valueOf(10));
        sessionDao.saveAll(List.of(session1, session2));

        Block<Movie> block = catalogService.findNowPlayingMovies(LocalDate.now(),0, 10);
        assertEquals(1, block.getItems().size());
    }

    @Test
    public void testFindMoviesByTitle() {
        Movie movie1 = createMovie("El Señor de los Anillos");
        Movie movie2 = createMovie("El Señor de los Anillos 2");
        Movie movie3 = createMovie("Star Wars");
        movieDao.saveAll(List.of(movie1, movie2, movie3));

        Block<Movie> block = catalogService.findMoviesByTitle("señor", 0, 10);

        assertEquals(2, block.getItems().size());
        assertFalse(block.getExistMoreItems());
    }

    // -------------------------------------------------------------------------
    // Tests visualizar la información detallada de una película (FUNC-2)
    // -------------------------------------------------------------------------

    @Test
    public void testFindMovie() throws InstanceNotFoundException {
        Movie movie = new Movie("Título", "Sinopsis", 120);
        movieDao.save(movie);
        Movie found = catalogService.findMovie(movie.getId());

        assertEquals(movie.getId(), found.getId());
        assertEquals(movie.getTitle(), found.getTitle());
        assertEquals(movie.getSummary(), found.getSummary());
        assertEquals(movie.getDuration(), found.getDuration());
    }

    @Test
    public void testFindMovieNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> catalogService.findMovie(-1L));
    }

    // -------------------------------------------------------------------------
    // Tests visualizar la información detallada de una sesión (FUNC-3)
    // -------------------------------------------------------------------------

    @Test
    public void testFindSessionByNonExistentId() {
        assertThrows(InstanceNotFoundException.class, () -> catalogService.findSession(-1L));
    }

    @Test
    public void testFindSessionById() throws InstanceNotFoundException, SessionAlreadyStartedException {

        Movie movie = createMovie("Avatar");
        Room room = createRoom("Sala 1");

        movieDao.save(movie);
        roomDao.save(room);

        Session session = new Session(movie, room, LocalDateTime.now().plusHours(2), new BigDecimal(10));
        sessionDao.save(session);

        Session foundSession = catalogService.findSession(session.getId());

        assertEquals(session, foundSession);
        assertEquals(movie, foundSession.getMovie());
        assertEquals(room, foundSession.getRoom());
    }

}