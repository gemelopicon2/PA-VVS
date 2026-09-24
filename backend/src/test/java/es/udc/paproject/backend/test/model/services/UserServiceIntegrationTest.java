package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.services.UserServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserDao userDao;

    @Test
    public void testSignUpSavesUserSuccessfully() throws Exception {
        User user = new User("newuser", "password", "First", "Last", "newuser@udc.es");
        
        userService.signUp(user);
        
        assertTrue(userDao.existsByUserName("newuser"));
    }

    @Test
    public void testSignUpThrowsDuplicateInstanceExceptionForExistingUser() throws Exception {
        User user1 = new User("duplicate", "password", "First", "Last", "dup1@udc.es");
        userService.signUp(user1);
        User user2 = new User("duplicate", "pass2", "F2", "L2", "dup2@udc.es");

        assertThrows(DuplicateInstanceException.class, () -> userService.signUp(user2));
    }

    @Test
    public void testLoginReturnsCorrectUser() throws Exception {
        User user = new User("loginuser", "password", "First", "Last", "login@udc.es");
        userService.signUp(user);
        
        User loggedInUser = userService.login("loginuser", "password");
        
        assertEquals("loginuser", loggedInUser.getUserName());
    }

    @Test
    public void testLoginThrowsExceptionForIncorrectPassword() throws Exception {
        User user = new User("wrongpassuser", "password", "First", "Last", "wrong@udc.es");
        userService.signUp(user);
        
        assertThrows(IncorrectLoginException.class, () -> userService.login("wrongpassuser", "wrong"));
    }

    @Test
    public void testUpdateProfileModifiesFirstName() throws Exception {
        User user = new User("updateuser", "password", "OldFirst", "Last", "update@udc.es");
        userService.signUp(user);
        
        User updatedUser = userService.updateProfile(user.getId(), "NewFirst", "Last", "update@udc.es");
        
        assertEquals("NewFirst", updatedUser.getFirstName());
    }
}