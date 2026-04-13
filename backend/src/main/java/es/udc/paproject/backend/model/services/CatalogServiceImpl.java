package es.udc.paproject.backend.model.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.MovieDao;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private MovieDao movieDao;

    @Override
    public List<Session> findNowPlayingMovies(LocalDate date) throws InvalidDateException {
        LocalDate today = LocalDate.now();

        if (date.isBefore(today) || date.isAfter(today.plusDays(6))) {
            throw new InvalidDateException();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Session> sessions = sessionDao.findByDateBetween(start, end);

        sessions.sort(Comparator.comparing((Session s) -> s.getMovie().getTitle())
                .thenComparing(Session::getDate));

        return sessions;
    }

    @Override
    public Movie findMovie(Long movieId) throws InstanceNotFoundException {
        return movieDao.findById(movieId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.movie", movieId));
    }

    @Override
    public Session findSession(Long sessionId) throws InstanceNotFoundException, SessionAlreadyStartedException {
        Session session = sessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.session", sessionId));

        if (session.getDate().isBefore(LocalDateTime.now())) {
            throw new SessionAlreadyStartedException();
        }

        return session;
    }
}