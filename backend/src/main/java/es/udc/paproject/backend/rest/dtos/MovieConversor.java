package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Movie;
import java.util.List;
import java.util.stream.Collectors;

public class MovieConversor {
    public static MovieCatalogDto toMovieCatalogDto(Movie movie) {
        return new MovieCatalogDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDuration(),
                movie.getSummary()
        );
    }

    public static List<MovieCatalogDto> toMovieCatalogDtos(List<Movie> movies) {
        return movies.stream().map(MovieConversor::toMovieCatalogDto).collect(Collectors.toList());
    }

    public static MovieDetailsDto toDetailsDto(Movie movie) {
        return new MovieDetailsDto(
                movie.getId(),
                movie.getTitle(),
                movie.getSummary(),
                movie.getDuration()
        );
    }
}