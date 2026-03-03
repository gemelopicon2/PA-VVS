package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;

public interface CatalogService {

    Session findSession(Long sessionId) throws InstanceNotFoundException;
    Session findSessionWithAvailableSeats(Long sessionId) throws InstanceNotFoundException;
    Block<Movie> findNowPlayingMovies(int page, int size);
    Block<Movie> findMoviesByTitle(String title, int page, int size);
    Movie findMovie(Long movieId) throws InstanceNotFoundException;

}
