package es.udc.paproject.backend.test.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PurchaseDaoTest {

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
        testUser = new User("userTest", "pass", "Name", "Last", "test@udc.es");
        userDao.save(testUser);

        Movie movie = new Movie("Title", "Summary", 120);
        movieDao.save(movie);

        Room room = new Room("Room 1", 50);
        roomDao.save(room);

        testSession = new Session(movie, room, LocalDateTime.now().plusDays(1), new BigDecimal("5.00"));
        sessionDao.save(testSession);
    }

    @Test
    public void testFindByUserIdOrderByDateDescReturnsCorrectSize() {
        Purchase purchase1 = new Purchase(testUser, testSession, 2, "1234567890123456", LocalDateTime.now());
        purchaseDao.save(purchase1);
        
        Slice<Purchase> slice = purchaseDao.findByUserIdOrderByDateDesc(testUser.getId(), PageRequest.of(0, 10));

        assertEquals(1, slice.getContent().size());
    }

    @Test
    public void testFindByUserIdOrderByDateDescReturnsInCorrectOrder() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        LocalDateTime present = LocalDateTime.now();
        
        purchaseDao.save(new Purchase(testUser, testSession, 1, "1234", past));
        purchaseDao.save(new Purchase(testUser, testSession, 1, "1234", present));
        
        Slice<Purchase> slice = purchaseDao.findByUserIdOrderByDateDesc(testUser.getId(), PageRequest.of(0, 10));

        assertTrue(slice.getContent().get(0).getDate().isAfter(slice.getContent().get(1).getDate()));
    }

    @Test
    public void testFindByUserIdOrderByDateDescRespectsPagination() {
        purchaseDao.save(new Purchase(testUser, testSession, 1, "1234", LocalDateTime.now()));
        purchaseDao.save(new Purchase(testUser, testSession, 1, "1234", LocalDateTime.now()));
        
        Slice<Purchase> slice = purchaseDao.findByUserIdOrderByDateDesc(testUser.getId(), PageRequest.of(0, 1));

        assertEquals(1, slice.getContent().size());
    }
}