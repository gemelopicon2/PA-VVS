package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.Block;
import es.udc.paproject.backend.model.services.CatalogService;
import es.udc.paproject.backend.rest.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    // FUNC-1: Ver cartelera
    @GetMapping("/movies")
    public BlockDto<MovieCatalogDto> findNowPlayingMovies(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Block<Movie> movieBlock;
        if (title != null && !title.trim().isEmpty()) {
            movieBlock = catalogService.findMoviesByTitle(title.trim(), page, size);
        } else {
            movieBlock = catalogService.findNowPlayingMovies(page, size);
        }

        return new BlockDto<>(
                MovieConversor.toMovieCatalogDtos(movieBlock.getItems()),
                movieBlock.getExistMoreItems()
        );
    }

    // FUNC-2: Detalle de la película
    @GetMapping("/movies/{movieId}")
    public MovieDetailsDto findMovie(@PathVariable Long movieId) throws InstanceNotFoundException {
        Movie movie = catalogService.findMovie(movieId);
        return MovieConversor.toMovieDetailsDto(movie);
    }

    // FUNC-3: Detalle de la sesión
    @GetMapping("/sessions/{sessionId}")
    public SessionDto findSession(@PathVariable Long sessionId) throws InstanceNotFoundException {
        Session session = catalogService.findSession(sessionId);
        return SessionConversor.toSessionDto(session);
    }
}