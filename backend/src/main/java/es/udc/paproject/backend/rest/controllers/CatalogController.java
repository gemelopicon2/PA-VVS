package es.udc.paproject.backend.rest.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.InvalidDateException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.services.CatalogService;
import es.udc.paproject.backend.rest.dtos.MovieCatalogDto;
import es.udc.paproject.backend.rest.dtos.MovieConversor;
import es.udc.paproject.backend.rest.dtos.MovieDetailsDto;
import es.udc.paproject.backend.rest.dtos.SessionConversor;
import es.udc.paproject.backend.rest.dtos.SessionDto;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    // FUNC-1: Ver cartelera
    @GetMapping("/movies")
    public List<MovieCatalogDto> findNowPlayingMovies(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws InvalidDateException {

        List<Session> sessions = catalogService.findNowPlayingMovies(date);
        return MovieConversor.toMovieCatalogDtos(sessions);
    }

    // FUNC-2: Detalle de la película
    @GetMapping("/movies/{id}")
    public MovieDetailsDto findMovie(@PathVariable Long id) throws InstanceNotFoundException {
        return MovieConversor.toMovieDetailsDto(catalogService.findMovie(id));
    }

    // FUNC-3: Detalle de la sesión
    @GetMapping("/sessions/{id}")
    public SessionDto findSession(@PathVariable Long id) throws InstanceNotFoundException, SessionAlreadyStartedException {
        return SessionConversor.toSessionDto(catalogService.findSession(id));
    }
}