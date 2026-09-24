package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PermissionCheckerImpl;

@ExtendWith(MockitoExtension.class)
public class PermissionCheckerTest {

    private static final Long NON_EXISTENT_ID = -1L;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PermissionCheckerImpl permissionChecker;

    @Test
    public void testCheckUser() throws InstanceNotFoundException {

        User user = new User("user", "password", "firstName", "lastName", "user@udc.es");
        user.setId(1L);

        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        assertSame(user, permissionChecker.checkUser(1L));

    }

    @Test
    public void testCheckNonExistentUser() {

        when(userDao.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        InstanceNotFoundException e = assertThrows(InstanceNotFoundException.class,
            () -> permissionChecker.checkUser(NON_EXISTENT_ID));

        assertEquals("project.entities.user", e.getName());
        assertEquals(NON_EXISTENT_ID, e.getKey());

    }
    @Test
    public void testCheckPurchaseOwnershipReturnsUser() throws Exception {
        // Arrange
        User user = new User("viewer", "password", "firstName", "lastName", "viewer@udc.es");
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        // Act (Fallará con un NullPointerException o aserción fallida porque devuelve null siempre)
        User result = permissionChecker.checkPurchaseOwnership(1L, 1L);

        // Assert
        assertNotNull(result, "El método de verificación de propiedad no debería devolver null");
    }

}
