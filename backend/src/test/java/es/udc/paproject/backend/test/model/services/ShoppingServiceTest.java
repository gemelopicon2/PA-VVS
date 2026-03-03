package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.MaxTicketsExceededException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.services.ShoppingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShoppingServiceTest {

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private EntityManager entityManager;

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
}