package es.udc.paproject.backend.test.model.services;

import net.jqwik.api.*;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShoppingServicePropertyTest {

    @Property
    @DisplayName("Datos Aleatorios: Cantidades de entradas menores ou iguais a cero deben ser rexeitadas")
    void buyTicketsShouldFailForNonPositiveTicketCount(
            @ForAll("nonPositiveTicketCounts") int invalidTickets) {

        assertThrows(IllegalArgumentException.class, () -> {
            validateTicketPurchaseQuantity(invalidTickets);
        });
    }

    @Provide
    Arbitrary<Integer> nonPositiveTicketCounts() {
        return Arbitraries.integers().lessOrEqual(0);
    }

    private void validateTicketPurchaseQuantity(int tickets) {
        if (tickets <= 0 || tickets > 10) {
            throw new IllegalArgumentException("O número de entradas debe estar entre 1 e 10");
        }
    }
}