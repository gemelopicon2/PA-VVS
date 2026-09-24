package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.MovieDao;
import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.entities.PurchaseDao;
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.RoomDao;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
// IMPORTANTE: Asegúrate de importar la interfaz de tu servicio real
// import es.udc.paproject.backend.model.services.ShoppingService; 
import es.udc.paproject.backend.model.services.ShoppingServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShoppingServiceIntegrationTest {

    // Cambia ShoppingServiceImpl por la interfaz ShoppingService si tu arquitectura lo requiere
    @Autowired
    private ShoppingServiceImpl shoppingService; 

    @Autowired
    private PurchaseDao purchaseDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SessionDao sessionDao;
    @Autowired
    private MovieDao movieDao;
    @Autowired
    private RoomDao roomDao;

    private User testUser;
    private Session testSession;

    @BeforeEach
    public void setUp() {
        testUser = new User("buyer", "pass", "Name", "Last", "buyer@udc.es");
        userDao.save(testUser);

        Movie movie = new Movie("Title", "Summary", 120);
        movieDao.save(movie);

        Room room = new Room("Room A", 10);
        roomDao.save(room);

        // Se crea la sesión con 10 tickets disponibles basados en la capacidad de la sala
        testSession = new Session(movie, room, LocalDateTime.now().plusDays(2), new BigDecimal("7.50"));
        sessionDao.save(testSession);
    }

    @Test
    public void testBuyTicketsDecrementsAvailableTickets() throws Exception {
        int ticketsToBuy = 3;
        
        shoppingService.buyTickets(testUser.getId(), testSession.getId(), ticketsToBuy, "1234567890123456");
        
        Session updatedSession = sessionDao.findById(testSession.getId()).get();
        assertEquals(7, updatedSession.getAvailableTickets());
    }

    @Test
    public void testBuyTicketsSavesPurchaseInDatabase() throws Exception {
        int ticketsToBuy = 3;
        
        Purchase purchase = shoppingService.buyTickets(testUser.getId(), testSession.getId(), ticketsToBuy, "1234567890123456");
        
        assertTrue(purchaseDao.findById(purchase.getId()).isPresent());
    }

    @Test
    public void testDeliverTicketsUpdatesDeliveryStatus() throws Exception {
        Purchase purchase = new Purchase(testUser, testSession, 2, "1234567890123456", LocalDateTime.now());
        purchaseDao.save(purchase);
        
        shoppingService.deliverTickets(purchase.getId(), "1234567890123456");
        
        Purchase updatedPurchase = purchaseDao.findById(purchase.getId()).get();
        assertTrue(updatedPurchase.isDelivered());
    }
    @Test
    public void testBuyTicketsWithNegativeAmountThrowsException() {
        // Arrange
        int negativeTickets = -5;
        
        // Act & Assert (Se espera que salte una excepción de validación o IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, 
            () -> shoppingService.buyTickets(testUser.getId(), testSession.getId(), negativeTickets, "1234567890123456"));
    }
}