package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.MovieDao;
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.services.CatalogServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CatalogServiceTest {

    private static final Long NON_EXISTENT_ID = -1L;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private MovieDao movieDao;

    @InjectMocks
    private CatalogServiceImpl catalogService;

    private Movie createMovie(Long id, String title) {
        Movie movie = new Movie(title, "summary", 120);
        movie.setId(id);
        return movie;
    }

    private Session createSession(Long id, Movie movie, LocalDateTime date) {
        Session session = new Session(movie, new Room("Sala", 50), date, new BigDecimal("7.50"));
        session.setId(id);
        return session;
    }

    // FUNC-1

    @Test
    public void testFindNowPlayingMoviesSortedByTitleAndDate() throws InvalidDateException {

        LocalDate date = LocalDate.now().plusDays(1);
        Movie movieA = createMovie(1L, "A movie");
        Movie movieB = createMovie(2L, "B movie");
        Session b1 = createSession(1L, movieB, date.atTime(18, 0));
        Session a2 = createSession(2L, movieA, date.atTime(22, 0));
        Session a1 = createSession(3L, movieA, date.atTime(16, 0));

        when(sessionDao.findByDateBetween(date.atStartOfDay(), date.atTime(23, 59, 59)))
            .thenReturn(new ArrayList<>(List.of(b1, a2, a1)));

        List<Session> sessions = catalogService.findNowPlayingMovies(date);

        assertEquals(List.of(a1, a2, b1), sessions);

    }

    @Test
    public void testFindNowPlayingMoviesToday() throws InvalidDateException {

        LocalDate today = LocalDate.now();

        when(sessionDao.findByDateBetween(today.atStartOfDay(), today.atTime(23, 59, 59)))
            .thenReturn(new ArrayList<>());

        assertTrue(catalogService.findNowPlayingMovies(today).isEmpty());

    }

    @Test
    public void testFindNowPlayingMoviesLastValidDay() throws InvalidDateException {

        LocalDate lastDay = LocalDate.now().plusDays(6);

        when(sessionDao.findByDateBetween(lastDay.atStartOfDay(), lastDay.atTime(23, 59, 59)))
            .thenReturn(new ArrayList<>());

        assertTrue(catalogService.findNowPlayingMovies(lastDay).isEmpty());

    }

    @Test
    public void testFindNowPlayingMoviesPastDate() {

        assertThrows(InvalidDateException.class,
            () -> catalogService.findNowPlayingMovies(LocalDate.now().minusDays(1)));
        verify(sessionDao, never()).findByDateBetween(any(), any());

    }

    @Test
    public void testFindNowPlayingMoviesTooFarDate() {

        assertThrows(InvalidDateException.class,
            () -> catalogService.findNowPlayingMovies(LocalDate.now().plusDays(7)));
        verify(sessionDao, never()).findByDateBetween(any(), any());

    }

    // FUNC-2

    @Test
    public void testFindMovie() throws InstanceNotFoundException {

        Movie movie = createMovie(1L, "movie");

        when(movieDao.findById(1L)).thenReturn(Optional.of(movie));

        assertSame(movie, catalogService.findMovie(1L));

    }

    @Test
    public void testFindNonExistentMovie() {

        when(movieDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        InstanceNotFoundException e = assertThrows(InstanceNotFoundException.class,
            () -> catalogService.findMovie(NON_EXISTENT_ID));

        assertEquals("project.entities.movie", e.getName());
        assertEquals(NON_EXISTENT_ID, e.getKey());

    }

    // FUNC-3

    @Test
    public void testFindSession() throws InstanceNotFoundException, SessionAlreadyStartedException {

        Session session = createSession(1L, createMovie(1L, "movie"), LocalDateTime.now().plusHours(2));

        when(sessionDao.findById(1L)).thenReturn(Optional.of(session));

        assertSame(session, catalogService.findSession(1L));

    }

    @Test
    public void testFindNonExistentSession() {

        when(sessionDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        InstanceNotFoundException e = assertThrows(InstanceNotFoundException.class,
            () -> catalogService.findSession(NON_EXISTENT_ID));

        assertEquals("project.entities.session", e.getName());
        assertEquals(NON_EXISTENT_ID, e.getKey());

    }

    @Test
    public void testFindStartedSession() {

        Session session = createSession(1L, createMovie(1L, "movie"), LocalDateTime.now().minusMinutes(1));

        when(sessionDao.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(SessionAlreadyStartedException.class, () -> catalogService.findSession(1L));

    }

}
