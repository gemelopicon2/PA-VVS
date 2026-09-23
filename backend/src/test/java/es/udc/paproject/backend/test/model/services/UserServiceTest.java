package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PermissionChecker;
import es.udc.paproject.backend.model.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final Long NON_EXISTENT_ID = -1L;

    @Mock
    private PermissionChecker permissionChecker;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User createUser(String userName) {
        User user = new User(userName, "password", "firstName", "lastName", userName + "@udc.es");
        user.setId(1L);
        return user;
    }

    @Test
    public void testSignUp() throws DuplicateInstanceException {

        User user = createUser("user");
        user.setRole(User.RoleType.SELLER);

        when(userDao.existsByUserName("user")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        userService.signUp(user);

        assertEquals("encoded", user.getPassword());
        assertEquals(User.RoleType.USER, user.getRole());
        verify(userDao).save(user);

    }

    @Test
    public void testSignUpDuplicatedUserName() {

        User user = createUser("user");

        when(userDao.existsByUserName("user")).thenReturn(true);

        DuplicateInstanceException e = assertThrows(DuplicateInstanceException.class,
            () -> userService.signUp(user));

        assertEquals("project.entities.user", e.getName());
        assertEquals("user", e.getKey());
        assertEquals("password", user.getPassword());
        verify(userDao, never()).save(any());

    }

    @Test
    public void testLogin() throws IncorrectLoginException {

        User user = createUser("user");
        user.setPassword("encoded");

        when(userDao.findByUserName("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);

        assertSame(user, userService.login("user", "password"));

    }

    @Test
    public void testLoginNonExistentUser() {

        when(userDao.findByUserName("user")).thenReturn(Optional.empty());

        IncorrectLoginException e = assertThrows(IncorrectLoginException.class,
            () -> userService.login("user", "password"));

        assertEquals("user", e.getUserName());
        assertEquals("password", e.getPassword());
        verify(passwordEncoder, never()).matches(any(), anyString());

    }

    @Test
    public void testLoginIncorrectPassword() {

        User user = createUser("user");
        user.setPassword("encoded");

        when(userDao.findByUserName("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        IncorrectLoginException e = assertThrows(IncorrectLoginException.class,
            () -> userService.login("user", "wrong"));

        assertEquals("user", e.getUserName());
        assertEquals("wrong", e.getPassword());

    }

    @Test
    public void testLoginFromId() throws InstanceNotFoundException {

        User user = createUser("user");

        when(permissionChecker.checkUser(1L)).thenReturn(user);

        assertSame(user, userService.loginFromId(1L));

    }

    @Test
    public void testLoginFromNonExistentId() throws InstanceNotFoundException {

        when(permissionChecker.checkUser(NON_EXISTENT_ID))
            .thenThrow(new InstanceNotFoundException("project.entities.user", NON_EXISTENT_ID));

        assertThrows(InstanceNotFoundException.class, () -> userService.loginFromId(NON_EXISTENT_ID));

    }

    @Test
    public void testUpdateProfile() throws InstanceNotFoundException {

        User user = createUser("user");

        when(permissionChecker.checkUser(1L)).thenReturn(user);

        User updatedUser = userService.updateProfile(1L, "newFirstName", "newLastName", "new@udc.es");

        assertSame(user, updatedUser);
        assertEquals("newFirstName", user.getFirstName());
        assertEquals("newLastName", user.getLastName());
        assertEquals("new@udc.es", user.getEmail());
        assertEquals("user", user.getUserName());

    }

    @Test
    public void testUpdateProfileNonExistentUser() throws InstanceNotFoundException {

        when(permissionChecker.checkUser(NON_EXISTENT_ID))
            .thenThrow(new InstanceNotFoundException("project.entities.user", NON_EXISTENT_ID));

        assertThrows(InstanceNotFoundException.class,
            () -> userService.updateProfile(NON_EXISTENT_ID, "a", "b", "c@udc.es"));

    }

    @Test
    public void testChangePassword() throws InstanceNotFoundException, IncorrectPasswordException {

        User user = createUser("user");
        user.setPassword("encodedOld");

        when(permissionChecker.checkUser(1L)).thenReturn(user);
        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");

        userService.changePassword(1L, "old", "new");

        assertEquals("encodedNew", user.getPassword());

    }

    @Test
    public void testChangePasswordIncorrectOldPassword() throws InstanceNotFoundException {

        User user = createUser("user");
        user.setPassword("encodedOld");

        when(permissionChecker.checkUser(1L)).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encodedOld")).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> userService.changePassword(1L, "wrong", "new"));
        assertEquals("encodedOld", user.getPassword());
        verify(passwordEncoder, never()).encode(any());

    }

    @Test
    public void testChangePasswordNonExistentUser() throws InstanceNotFoundException {

        when(permissionChecker.checkUser(NON_EXISTENT_ID))
            .thenThrow(new InstanceNotFoundException("project.entities.user", NON_EXISTENT_ID));

        assertThrows(InstanceNotFoundException.class,
            () -> userService.changePassword(NON_EXISTENT_ID, "old", "new"));

    }

}
