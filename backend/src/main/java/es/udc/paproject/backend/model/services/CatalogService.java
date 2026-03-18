package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;

import java.time.LocalDate;

public interface CatalogService {

    Session findSession(Long sessionId) throws InstanceNotFoundException, SessionAlreadyStartedException;
    Session findSessionWithAvailableSeats(Long sessionId) throws InstanceNotFoundException;
    Block<Movie> findNowPlayingMovies(LocalDate date, int page, int size) throws InvalidDateException;
    Block<Movie> findMoviesByTitle(String title, int page, int size);
    Movie findMovie(Long movieId) throws InstanceNotFoundException;

}
