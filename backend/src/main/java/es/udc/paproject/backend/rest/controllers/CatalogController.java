package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.services.Block;
import es.udc.paproject.backend.model.services.CatalogService;
import es.udc.paproject.backend.rest.common.ErrorsDto;
import es.udc.paproject.backend.rest.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private MessageSource messageSource;

    // FUNC-1: Ver cartelera
    @GetMapping("/movies")
    public BlockDto<MovieCatalogDto> findNowPlayingMovies(
            @RequestParam LocalDate date,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
            throws InvalidDateException {

        Block<Movie> movieBlock;
        if (title != null && !title.trim().isEmpty()) {
            movieBlock = catalogService.findMoviesByTitle(title.trim(), page, size);
        } else {
            movieBlock = catalogService.findNowPlayingMovies(date, page, size);
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
    public SessionDto findSession(@PathVariable Long sessionId) throws InstanceNotFoundException, SessionAlreadyStartedException {
        Session session = catalogService.findSession(sessionId);
        return SessionConversor.toSessionDto(session);
    }

    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handleInvalidDateException(InvalidDateException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(
                "project.exceptions.InvalidDateException", null,
                "project.exceptions.InvalidDateException", locale
        );
        return new ErrorsDto(errorMessage);
    }


}