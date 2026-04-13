package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Session;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieConversor {

    private MovieConversor() {}

    public static List<MovieCatalogDto> toMovieCatalogDtos(List<Session> sessions) {
        Map<Movie, List<Session>> grouped = sessions.stream()
                .collect(Collectors.groupingBy(Session::getMovie, LinkedHashMap::new, Collectors.toList()));

        List<MovieCatalogDto> dtos = new ArrayList<>();

        for (Map.Entry<Movie, List<Session>> entry : grouped.entrySet()) {
            Movie movie = entry.getKey();

            List<MovieCatalogDto.SessionSummaryDto> sessionDtos = entry.getValue().stream()
                    .map(s -> new MovieCatalogDto.SessionSummaryDto(
                            s.getId(),
                            s.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    ))
                    .collect(Collectors.toList());

            dtos.add(new MovieCatalogDto(movie.getId(), movie.getTitle(), sessionDtos));
        }

        return dtos;
    }

    public static final MovieDetailsDto toMovieDetailsDto(Movie movie) {
        return new MovieDetailsDto(movie.getId(), movie.getTitle(), movie.getSummary(), movie.getDuration());
    }
}