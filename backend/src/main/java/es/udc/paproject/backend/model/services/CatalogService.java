package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;

import java.time.LocalDate;
import java.util.List;

public interface CatalogService {

    List<Session> findNowPlayingMovies(LocalDate date) throws InvalidDateException;
    Movie findMovie(Long movieId) throws InstanceNotFoundException;
    Session findSession(Long sessionId) throws InstanceNotFoundException, SessionAlreadyStartedException;

}
