package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.PurchaseDao;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.SessionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.ShoppingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingServiceImplTest {

    @Mock
    private SessionDao sessionDao;

    @Mock
    private PurchaseDao purchaseDao;

    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    // --- VALORES FRONTEIRA E PARTICIÓNS INVÁLIDAS ---

    @Test
    @DisplayName("Valores Fronteira: Comprar 0 entradas (xusto por debaixo do mínimo permitido) debe lanzar IllegalArgumentException")
    void testBuyTicketsBoundaryZeroTicketsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingService.buyTickets(1L, 1L, 0, "1234567890123456");
        });
    }

    @Test
    @DisplayName("Valores Fronteira: Comprar -1 entradas (partición negativa) debe lanzar IllegalArgumentException")
    void testBuyTicketsNegativeTicketsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingService.buyTickets(1L, 1L, -1, "1234567890123456");
        });
    }

    @Test
    @DisplayName("Valores Fronteira: Comprar 11 entradas (xusto por encima do máximo permitido) debe lanzar IllegalArgumentException")
    void testBuyTicketsBoundaryExceedMaxTicketsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingService.buyTickets(1L, 1L, 11, "1234567890123456");
        });
    }

    @Test
    @DisplayName("Particións Equivalentes: Sesión inexistente debe lanzar InstanceNotFoundException")
    void testBuyTicketsNonExistentSessionThrowsException() {
        when(sessionDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> {
            shoppingService.buyTickets(1L, 999L, 2, "1234567890123456");
        });
    }
}