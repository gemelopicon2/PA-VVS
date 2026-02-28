package es.udc.paproject.backend.model.services;

import java.util.Optional;

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

    @Override
    public Session findSession(Long sessionId) throws InstanceNotFoundException {

        Optional<Session> session = sessionDao.findById(sessionId);

        if (!session.isPresent()) {
            throw new InstanceNotFoundException("project.entities.session", sessionId);
        }

        return session.get();
    }

    @Autowired
    private MovieDao movieDao;

    @Override
    public Block<Movie> findNowPlayingMovies(int page, int size) {
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieDao.findDistinctBySessionsDateAfter(today.atStartOfDay(), pageable);
        return new Block<>(moviePage.getContent(), moviePage.hasNext());
    }

}