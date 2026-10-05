package es.udc.paproject.backend.test.model.services;


import org.junit.jupiter.api.DisplayName;
import net.jqwik.api.*;
import net.jqwik.time.api.Dates;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CatalogServicePropertyTest {

    @Property
    @DisplayName("Rangos de datas: Se a data de inicio é posterior á de fin debe lanzar IllegalArgumentException")
    void invalidDateRangesShouldBeRejected(
            @ForAll("dates") LocalDate startDate,
            @ForAll("dates") LocalDate endDate) {

        Assume.that(startDate.isAfter(endDate));

        assertThrows(IllegalArgumentException.class, () -> {
            validateDateRange(startDate, endDate);
        });
    }

    @Provide
    Arbitrary<LocalDate> dates() {
        return Dates.dates().between(LocalDate.of(2025, 1, 1), LocalDate.of(2030, 12, 31));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("A data de inicio non pode ser posterior á data de fin");
        }
    }
}