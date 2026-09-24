package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.CatalogService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    @Test
    public void testFindNowPlayingMoviesReturnsOkStatus() throws Exception {
        when(catalogService.findNowPlayingMovies(any(LocalDate.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/catalog/movies")
                .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindMovieReturnsOkStatus() throws Exception {
        Movie movie = new Movie("Title", "Summary", 120);
        movie.setId(1L);
        when(catalogService.findMovie(1L)).thenReturn(movie);

        mockMvc.perform(get("/catalog/movies/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindMovieWithNonExistentIdReturnsNotFound() throws Exception {
        when(catalogService.findMovie(999L)).thenThrow(new InstanceNotFoundException("Movie", 999L));

        mockMvc.perform(get("/catalog/movies/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindSessionReturnsOkStatus() throws Exception {
        Movie movie = new Movie("Title", "Summary", 120);
        Room room = new Room("Room", 50);
        Session session = new Session(movie, room, LocalDateTime.now().plusDays(1), new BigDecimal("5.00"));
        session.setId(1L);
        when(catalogService.findSession(1L)).thenReturn(session);

        mockMvc.perform(get("/catalog/sessions/1"))
                .andExpect(status().isOk());
    }
}