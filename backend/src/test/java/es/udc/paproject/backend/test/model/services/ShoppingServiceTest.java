package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.ShoppingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.services.Block;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShoppingServiceTest {

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private PurchaseDao purchaseDao;

    private User createUser(String userName) {
        User user = new User(userName, "password", "Pepe", "Lopez",
                userName + "@ejemplo.com");
        user.setRole(User.RoleType.USER);
        entityManager.persist(user);
        return user;
    }

    private Room createRoom(String name, int capacity) {
        Room room = new Room(name, capacity);
        entityManager.persist(room);
        return room;
    }

    private Movie createMovie(String title) {
        Movie movie = new Movie(title, "Gran pelicula", 120);
        entityManager.persist(movie);
        return movie;
    }

    private Session createSession(Movie movie, Room room, LocalDateTime startDateTime, String price) {
        Session session = new Session(movie, room, startDateTime, new BigDecimal(price));
        entityManager.persist(session);
        return session;
    }

    private Purchase createPurchase(User user, Session session, int tickets, String creditCard) {
        Purchase purchase = new Purchase(user, session, tickets, creditCard, LocalDateTime.now());
        entityManager.persist(purchase);
        entityManager.flush();
        return purchase;
    }

    // -------------------------------------------------------------------------
    // Tests comprar tickets (FUNC-4)
    // -------------------------------------------------------------------------

    @Test
    public void testBuyTicketsSuccess() throws Exception {
        User user = createUser("testviewer1");
        Room room = createRoom("Room 1", 100);
        Movie movie = createMovie("Test Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        Purchase purchase = shoppingService.buyTickets(user.getId(), session.getId(), 2, "1234567890123456");

        assertNotNull(purchase);
        assertNotNull(purchase.getId());
        assertEquals(2, purchase.getTickets());
        assertEquals("1234567890123456", purchase.getCreditCard());
        assertEquals(user.getId(), purchase.getUser().getId());
        assertEquals(session.getId(), purchase.getSession().getId());
        assertFalse(purchase.isDelivered());
        assertNotNull(purchase.getDate());
    }

    @Test
    public void testBuyTicketsUserNotFound() {
        Room room = createRoom("Room 1", 100);
        Movie movie = createMovie("Test Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        assertThrows(InstanceNotFoundException.class, () -> {
            shoppingService.buyTickets(999L, session.getId(), 2, "1234567890123456");
        });
    }

    @Test
    public void testBuyTicketsSessionNotFound() {
        User user = createUser("testviewer2");

        assertThrows(InstanceNotFoundException.class, () -> {
            shoppingService.buyTickets(user.getId(), 999L, 2, "1234567890123456");
        });
    }

    @Test
    public void testBuyTicketsSessionAlreadyStarted() {
        User user = createUser("testviewer3");
        Room room = createRoom("Room 2", 100);
        Movie movie = createMovie("Past Movie");
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
        Session session = createSession(movie, room, pastDateTime, "10.00");

        assertThrows(SessionAlreadyStartedException.class, () -> {
            shoppingService.buyTickets(user.getId(), session.getId(), 2, "1234567890123456");
        });
    }

    @Test
    public void testBuyTicketsNotEnoughSeats() throws Exception {
        User user1 = createUser("testviewer4");
        User user2 = createUser("testviewer5");
        Room room = createRoom("Room 3", 5);
        Movie movie = createMovie("Popular Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        shoppingService.buyTickets(user1.getId(), session.getId(), 3, "1234567890123456");

        MaxTicketsExceededException exception = assertThrows(MaxTicketsExceededException.class, () -> {
            shoppingService.buyTickets(user2.getId(), session.getId(), 3, "9876543210987654");
        });

        assertEquals(3, exception.getRequested());
        assertEquals(2, exception.getAvailable());
    }

    @Test
    public void testBuyTicketsExactlyAvailableSeats() throws Exception {
        User user1 = createUser("testviewer6");
        User user2 = createUser("testviewer7");
        Room room = createRoom("Room 4", 5);
        Movie movie = createMovie("Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        shoppingService.buyTickets(user1.getId(), session.getId(), 3, "1111111111111111");

        Purchase purchase = shoppingService.buyTickets(user2.getId(), session.getId(), 2, "2222222222222222");

        assertNotNull(purchase);
        assertEquals(2, purchase.getTickets());
    }

    @Test
    public void testBuyTicketsMultiplePurchasesSameUser() throws Exception {
        User user = createUser("testviewer8");
        Room room = createRoom("Room 5", 50);
        Movie movie = createMovie("Great Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(2);
        Session session = createSession(movie, room, futureDateTime, "12.50");

        Purchase purchase1 = shoppingService.buyTickets(user.getId(), session.getId(), 3, "1111111111111111");
        Purchase purchase2 = shoppingService.buyTickets(user.getId(), session.getId(), 2, "2222222222222222");

        assertNotNull(purchase1);
        assertNotNull(purchase2);
        assertNotEquals(purchase1.getId(), purchase2.getId());
        assertEquals(3, purchase1.getTickets());
        assertEquals(2, purchase2.getTickets());
    }

    @Test
    public void testBuyTicketsDoesNotValidateRange() throws Exception {

        User user = createUser("testviewer9");
        Room room = createRoom("Room 6", 100);
        Movie movie = createMovie("Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        Purchase purchase = shoppingService.buyTickets(user.getId(), session.getId(), 0, "1234567890123456");

        assertNotNull(purchase);
        assertEquals(0, purchase.getTickets());
    }

    // -------------------------------------------------------------------------
    // Tests visualizar el histórico de compras (FUNC-5)
    // -------------------------------------------------------------------------

    @Test
    public void testGetPurchaseHistory() throws Exception {
        User user = createUser("user");
        Movie movie = new Movie("Avatar", "Resumen", 120);
        movieDao.save(movie);
        Room room = new Room("Sala 1", 100);
        roomDao.save(room);
        Session session = new Session(movie, room, LocalDateTime.now().plusDays(1), new BigDecimal("10.00"));
        sessionDao.save(session);
        Purchase oldPurchase = new Purchase(user, session, 2, "1234567890123456", LocalDateTime.now().minusDays(2));
        purchaseDao.save(oldPurchase);
        Purchase recentPurchase = new Purchase(user, session, 3, "1234567890123456", LocalDateTime.now().minusDays(1));
        purchaseDao.save(recentPurchase);
        Block<Purchase> block = shoppingService.getPurchaseHistory(user.getId(), 0, 2);
        assertEquals(2, block.getItems().size());
        assertEquals(recentPurchase.getId(), block.getItems().get(0).getId());
        assertEquals(oldPurchase.getId(), block.getItems().get(1).getId());
        assertFalse(block.getExistMoreItems());
    }

    @Test
    public void testGetPurchaseHistoryMoreThanTwo() throws Exception {
        User user = createUser("user");
        Movie movie = new Movie("Avatar", "Resumen", 130);
        movieDao.save(movie);
        Room room = new Room("Sala 1", 100);
        roomDao.save(room);
        Session session = new Session(movie, room, LocalDateTime.now().plusDays(1), new BigDecimal("10.00"));
        sessionDao.save(session);

        Purchase p1 = new Purchase(user, session, 1, "1111", LocalDateTime.now().minusDays(3));
        Purchase p2 = new Purchase(user, session, 2, "2222", LocalDateTime.now().minusDays(2));
        Purchase p3 = new Purchase(user, session, 3, "3333", LocalDateTime.now().minusDays(1));
        purchaseDao.saveAll(List.of(p1, p2, p3));

        Block<Purchase> block1 = shoppingService.getPurchaseHistory(user.getId(), 0, 2);
        assertEquals(2, block1.getItems().size());
        assertEquals(p3.getId(), block1.getItems().get(0).getId());
        assertEquals(p2.getId(), block1.getItems().get(1).getId());
        assertTrue(block1.getExistMoreItems());
        Block<Purchase> block2 = shoppingService.getPurchaseHistory(user.getId(), 1, 2);
        assertEquals(1, block2.getItems().size());
        assertEquals(p1.getId(), block2.getItems().get(0).getId());
        assertFalse(block2.getExistMoreItems());
    }

    @Test
    public void testGetPurchaseHistoryUserNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> shoppingService.getPurchaseHistory(-1L, 0, 2));
    }

    // =========================================================================
    // TESTS FUNC-6: Deliver Tickets
    // =========================================================================

    @Test
    public void testDeliverTicketsSuccess() throws Exception {
        User viewer = createUser("viewer1");
        Room room = createRoom("Room 1", 100);
        Movie movie = createMovie("Test Movie");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        Purchase purchase = createPurchase(viewer, session, 2, "1234567890123456");

        shoppingService.deliverTickets(purchase.getId(), "1234567890123456");

        assertTrue(purchase.isDelivered());
    }

    @Test
    public void testDeliverTicketsPurchaseNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> {
            shoppingService.deliverTickets(999L, "1234567890123456");
        });
    }

    @Test
    public void testDeliverTicketsIncorrectCreditCard() {
        User viewer = createUser("viewer2");
        Room room = createRoom("Room 2", 100);
        Movie movie = createMovie("Movie 2");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        Purchase purchase = createPurchase(viewer, session, 2, "1234567890123456");

        assertThrows(IncorrectCreditCardException.class, () -> {
            shoppingService.deliverTickets(purchase.getId(), "9999999999999999");
        });

        entityManager.refresh(purchase);
        assertFalse(purchase.isDelivered());
    }

    @Test
    public void testDeliverTicketsSessionAlreadyStarted() {
        User viewer = createUser("viewer3");
        Room room = createRoom("Room 3", 100);
        Movie movie = createMovie("Movie 3");
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
        Session session = createSession(movie, room, pastDateTime, "10.00");

        Purchase purchase = createPurchase(viewer, session, 2, "1234567890123456");

        assertThrows(SessionAlreadyStartedException.class, () -> {
            shoppingService.deliverTickets(purchase.getId(), "1234567890123456");
        });

        entityManager.refresh(purchase);
        assertFalse(purchase.isDelivered());
    }

    @Test
    public void testDeliverTicketsAlreadyDelivered() throws Exception {
        User viewer = createUser("viewer4");
        Room room = createRoom("Room 4", 100);
        Movie movie = createMovie("Movie 4");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        Purchase purchase = createPurchase(viewer, session, 2, "1234567890123456");

        shoppingService.deliverTickets(purchase.getId(), "1234567890123456");

        assertThrows(TicketsAlreadyDeliveredException.class, () -> {
            shoppingService.deliverTickets(purchase.getId(), "1234567890123456");
        });
    }

    @Test
    public void testDeliverTicketsMultiplePurchasesSameSession() throws Exception {
        User viewer1 = createUser("viewer5");
        User viewer2 = createUser("viewer6");
        Room room = createRoom("Room 5", 100);
        Movie movie = createMovie("Movie 5");
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        Session session = createSession(movie, room, futureDateTime, "10.00");

        Purchase purchase1 = createPurchase(viewer1, session, 2, "1111111111111111");
        Purchase purchase2 = createPurchase(viewer2, session, 3, "2222222222222222");

        shoppingService.deliverTickets(purchase1.getId(), "1111111111111111");

        assertTrue(purchase1.isDelivered());
        assertFalse(purchase2.isDelivered());

        shoppingService.deliverTickets(purchase2.getId(), "2222222222222222");

        assertTrue(purchase2.isDelivered());
    }
}