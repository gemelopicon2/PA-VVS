package es.udc.paproject.backend.test.performance;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

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
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.Purchase;
import java.math.BigDecimal;

import es.udc.paproject.backend.model.entities.MovieDao;
import es.udc.paproject.backend.model.entities.RoomDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.services.ShoppingService;
import es.udc.paproject.backend.model.services.UserService;

@SpringBootTest 
@ActiveProfiles("test")
@Transactional 
public class ShoppingServicePerformanceTest {

    private static EtmMonitor monitor;

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private RoomDao roomDao;

    @BeforeAll 
    public static void setUpEtm(){
        BasicEtmConfigurator.configure();
        monitor = EtmManager.getEtmMonitor();
        monitor.start();
    }

    @AfterAll 
    public static void finishEtm(){
        monitor.render(new SimpleTextRenderer());
        monitor.stop();
    }

    @Test 
    public void testBuyTicketsPerformance() throws Exception{

        //Datos de prueba previos
        User user = new User("buyerUser","password123","Buyer","Test","buyer@test.com");
        userService.signUp(user);
        Movie movie = new Movie("Compras JETM", "Sinopsis",100);
        movieDao.save(movie);
        int capacity = 100;
        Room usedRoom = new Room("Sala", capacity);
        roomDao.save(usedRoom);
        Session session = new Session(movie,usedRoom,LocalDateTime.now().plusDays(10),new BigDecimal("7.50"));

        sessionDao.save(session);
        String creditCardNumber = "1234567890123456";

        //Bucle de rendimiento
        for(int i = 0; i <100;i++){
            var point = monitor.createPoint("ShoppingService:buyTickets");

            try{
                Purchase purchase = shoppingService.buyTickets(user.getId(), session.getId(), 1, creditCardNumber);
            }finally{
                point.collect();
            }
        }

    }
    
}
