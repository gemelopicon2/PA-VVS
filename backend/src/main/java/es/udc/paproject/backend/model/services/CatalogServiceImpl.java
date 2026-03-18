package es.udc.paproject.backend.model.services;

import java.time.LocalDateTime;
import java.util.Optional;

import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.MovieDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private MovieDao movieDao;

    @Override
    public Session findSession(Long sessionId) throws InstanceNotFoundException, SessionAlreadyStartedException {

        Optional<Session> session = sessionDao.findById(sessionId);

        if (!session.isPresent()) {
            throw new InstanceNotFoundException("project.entities.session", sessionId);
        }
        if (session.get().getDate().isBefore(LocalDateTime.now())) {
            throw new SessionAlreadyStartedException();
        }

        return session.get();
    }

    @Override
    public Session findSessionWithAvailableSeats(Long sessionId) throws InstanceNotFoundException{
        return null;
    }



    @Override
    public Block<Movie> findNowPlayingMovies(LocalDate date, int page, int size) throws InvalidDateException {
        LocalDate today = LocalDate.now();

        if (date.isBefore(today) || date.isAfter(today.plusDays(6))) {
            throw new InvalidDateException();
        }

        LocalDateTime start = date.isEqual(today) ? LocalDateTime.now() : date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieDao.findDistinctBySessionsDateBetween(start, end, pageable);
        return new Block<>(moviePage.getContent(), moviePage.hasNext());
    }

    @Override
    public Block<Movie> findMoviesByTitle(String title, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieDao.findByTitleContainingIgnoreCase(title, pageable);

        return new Block<>(moviePage.getContent(), moviePage.hasNext());
    }

    @Override
    public Movie findMovie(Long movieId) throws InstanceNotFoundException {
        return movieDao.findById(movieId).orElseThrow(() -> new InstanceNotFoundException("project.entities.movie", movieId));
    }
}