package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.entities.PurchaseDao;
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.IncorrectCreditCardException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.MaxTicketsExceededException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.exceptions.TicketsAlreadyDeliveredException;
import es.udc.paproject.backend.model.services.Block;
import es.udc.paproject.backend.model.services.ShoppingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ShoppingServiceTest {

    private static final Long NON_EXISTENT_ID = -1L;
    private static final String CREDIT_CARD = "1234567812345678";
    private static final String OTHER_CREDIT_CARD = "8765432187654321";

    @Mock
    private UserDao userDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private PurchaseDao purchaseDao;

    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    private User createUser() {
        User user = new User("viewer", "password", "firstName", "lastName", "viewer@udc.es");
        user.setId(1L);
        user.setRole(User.RoleType.USER);
        return user;
    }

    private Session createSession(LocalDateTime date, int capacity) {
        Session session = new Session(new Movie("movie", "summary", 120), new Room("Sala", capacity), date,
            new BigDecimal("7.50"));
        session.setId(1L);
        return session;
    }

    private Purchase createPurchase(Session session, boolean delivered) {
        Purchase purchase = new Purchase(createUser(), session, 2, CREDIT_CARD, LocalDateTime.now());
        purchase.setId(1L);
        purchase.setDelivered(delivered);
        return purchase;
    }

    // FUNC-4

    @Test
    public void testBuyTickets() throws Exception {

        User user = createUser();
        Session session = createSession(LocalDateTime.now().plusDays(1), 10);
        LocalDateTime before = LocalDateTime.now();

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(sessionDao.findById(1L)).thenReturn(Optional.of(session));
        when(purchaseDao.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        Purchase purchase = shoppingService.buyTickets(1L, 1L, 3, CREDIT_CARD);

        assertSame(user, purchase.getUser());
        assertSame(session, purchase.getSession());
        assertEquals(3, purchase.getTickets());
        assertEquals(CREDIT_CARD, purchase.getCreditCard());
        assertFalse(purchase.isDelivered());
        assertFalse(purchase.getDate().isBefore(before));
        assertFalse(purchase.getDate().isAfter(LocalDateTime.now()));
        assertEquals(7, session.getAvailableTickets());
        verify(purchaseDao).save(purchase);

    }

    @Test
    public void testBuyAllAvailableTickets() throws Exception {

        Session session = createSession(LocalDateTime.now().plusDays(1), 5);

        when(userDao.findById(1L)).thenReturn(Optional.of(createUser()));
        when(sessionDao.findById(1L)).thenReturn(Optional.of(session));
        when(purchaseDao.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        shoppingService.buyTickets(1L, 1L, 5, CREDIT_CARD);

        assertEquals(0, session.getAvailableTickets());

    }

    @Test
    public void testBuyTooManyTickets() {

        Session session = createSession(LocalDateTime.now().plusDays(1), 5);

        when(userDao.findById(1L)).thenReturn(Optional.of(createUser()));
        when(sessionDao.findById(1L)).thenReturn(Optional.of(session));

        MaxTicketsExceededException e = assertThrows(MaxTicketsExceededException.class,
            () -> shoppingService.buyTickets(1L, 1L, 6, CREDIT_CARD));

        assertEquals(6, e.getRequested());
        assertEquals(5, e.getAvailable());
        assertEquals(5, session.getAvailableTickets());
        verify(purchaseDao, never()).save(any());

    }

    @Test
    public void testBuyTicketsStartedSession() {

        Session session = createSession(LocalDateTime.now().minusMinutes(1), 5);

        when(userDao.findById(1L)).thenReturn(Optional.of(createUser()));
        when(sessionDao.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(SessionAlreadyStartedException.class,
            () -> shoppingService.buyTickets(1L, 1L, 1, CREDIT_CARD));

        assertEquals(5, session.getAvailableTickets());
        verify(purchaseDao, never()).save(any());

    }

    @Test
    public void testBuyTicketsNonExistentUser() {

        when(userDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        InstanceNotFoundException e = assertThrows(InstanceNotFoundException.class,
            () -> shoppingService.buyTickets(NON_EXISTENT_ID, 1L, 1, CREDIT_CARD));

        assertEquals("project.entities.user", e.getName());
        verify(sessionDao, never()).findById(any());
        verify(purchaseDao, never()).save(any());

    }

    @Test
    public void testBuyTicketsNonExistentSession() {

        when(userDao.findById(1L)).thenReturn(Optional.of(createUser()));
        when(sessionDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        InstanceNotFoundException e = assertThrows(InstanceNotFoundException.class,
            () -> shoppingService.buyTickets(1L, NON_EXISTENT_ID, 1, CREDIT_CARD));

        assertEquals("project.entities.session", e.getName());
        assertEquals(NON_EXISTENT_ID, e.getKey());
        verify(purchaseDao, never()).save(any());

    }

    // FUNC-5

    @Test
    public void testGetPurchaseHistory() throws InstanceNotFoundException {

        Session session = createSession(LocalDateTime.now().plusDays(1), 10);
        List<Purchase> purchases = List.of(createPurchase(session, false), createPurchase(session, true));
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);

        when(userDao.findById(1L)).thenReturn(Optional.of(createUser()));
        when(purchaseDao.findByUserIdOrderByDateDesc(eq(1L), any(Pageable.class)))
            .thenReturn(new SliceImpl<>(purchases, Pageable.ofSize(2), true));

        Block<Purchase> block = shoppingService.getPurchaseHistory(1L, 3, 2);

        assertEquals(purchases, block.getItems());
        assertTrue(block.getExistMoreItems());
        verify(purchaseDao).findByUserIdOrderByDateDesc(eq(1L), pageable.capture());
        assertEquals(3, pageable.getValue().getPageNumber());
        assertEquals(2, pageable.getValue().getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getValue().getSort().getOrderFor("date").getDirection());

    }

    @Test
    public void testGetEmptyPurchaseHistory() throws InstanceNotFoundException {

        when(userDao.findById(1L)).thenReturn(Optional.of(createUser()));
        when(purchaseDao.findByUserIdOrderByDateDesc(eq(1L), any(Pageable.class)))
            .thenReturn(new SliceImpl<>(List.of(), Pageable.ofSize(2), false));

        Block<Purchase> block = shoppingService.getPurchaseHistory(1L, 0, 2);

        assertTrue(block.getItems().isEmpty());
        assertFalse(block.getExistMoreItems());

    }

    @Test
    public void testGetPurchaseHistoryNonExistentUser() {

        when(userDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class,
            () -> shoppingService.getPurchaseHistory(NON_EXISTENT_ID, 0, 2));
        verify(purchaseDao, never()).findByUserIdOrderByDateDesc(any(), any());

    }

    // FUNC-6

    @Test
    public void testDeliverTickets() throws Exception {

        Purchase purchase = createPurchase(createSession(LocalDateTime.now().plusHours(1), 10), false);

        when(purchaseDao.findById(1L)).thenReturn(Optional.of(purchase));

        shoppingService.deliverTickets(1L, CREDIT_CARD);

        assertTrue(purchase.isDelivered());

    }

    @Test
    public void testDeliverTicketsNonExistentPurchase() {

        when(purchaseDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        InstanceNotFoundException e = assertThrows(InstanceNotFoundException.class,
            () -> shoppingService.deliverTickets(NON_EXISTENT_ID, CREDIT_CARD));

        assertEquals("project.entities.purchase", e.getName());
        assertEquals(NON_EXISTENT_ID, e.getKey());

    }

    @Test
    public void testDeliverTicketsIncorrectCreditCard() {

        Purchase purchase = createPurchase(createSession(LocalDateTime.now().plusHours(1), 10), false);

        when(purchaseDao.findById(1L)).thenReturn(Optional.of(purchase));

        assertThrows(IncorrectCreditCardException.class,
            () -> shoppingService.deliverTickets(1L, OTHER_CREDIT_CARD));
        assertFalse(purchase.isDelivered());

    }

    @Test
    public void testDeliverTicketsStartedSession() {

        Purchase purchase = createPurchase(createSession(LocalDateTime.now().minusMinutes(1), 10), false);

        when(purchaseDao.findById(1L)).thenReturn(Optional.of(purchase));

        assertThrows(SessionAlreadyStartedException.class,
            () -> shoppingService.deliverTickets(1L, CREDIT_CARD));
        assertFalse(purchase.isDelivered());

    }

    @Test
    public void testDeliverTicketsAlreadyDelivered() {

        Purchase purchase = createPurchase(createSession(LocalDateTime.now().plusHours(1), 10), true);

        when(purchaseDao.findById(1L)).thenReturn(Optional.of(purchase));

        assertThrows(TicketsAlreadyDeliveredException.class,
            () -> shoppingService.deliverTickets(1L, CREDIT_CARD));

    }

}
