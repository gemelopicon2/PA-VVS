package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.MovieDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.CatalogService;

@SpringBootTest 
@ActiveProfiles("test")
@Transactional 
public class CatalogServicePerformanceTest {

    private static EtmMonitor monitor;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private MovieDao movieDao;

    @BeforeAll 
    public static void setUpEtm(){
        //Iniciaizar el monitor de JETM
        BasicEtmConfigurator.configure();
        monitor = EtmManager.getEtmMonitor();
        monitor.start();
    }

    @AfterAll 
    public static void finishEtm(){
        //Mostrar los resultados de rendimiento
        monitor.render(new SimpleTextRenderer());
        monitor.stop();
    }

    @Test 
    public void testFindMoviePerformance () throws InstanceNotFoundException{

        Movie movie = new Movie("Pelicula Test JETM" ,"Sinopsis de prueba", 120);

        movieDao.save(movie);

        Long movieId = 1L;
        
        
            //Ejecutamos la operación varias veces para obtener métricas representativas
            for(int i = 0; i < 100; i++){

                //Se envuelve la llamada al servicio dentro del punto de medición JETM
                var point = monitor.createPoint("CatalogService:findMovie");

                try{
                    Movie foundMovie = catalogService.findMovie(movieId);
                    assertNotNull(movie);

                }finally{
                    //Recolectamos las métricas
                    point.collect();
                }
            }
    }
}
    
