package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("Sprint 4: Юніт-тести UT-13–UT-16 (Виконавець: Борис Марія)")
class BorysTest {

    private BookingValidator validator;
    private PriceCalculator calc;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();
        calc = new PriceCalculator();
    }

    // Дефект модуля: рівно 30 ночей помилково відхиляються. Issue: #19
    @Test
    @DisplayName("UT-13 | BL-10: бронювання рівно на 30 ночей допустиме")
    void thirtyNightsAreAllowed() {
        var errors = validator.validate(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 10, 1),
                2,
                2,
                LocalDate.of(2026, 8, 20)
        );

        assertFalse(
                errors.contains(BookingError.NIGHTS_OUT_OF_RANGE),
                "BL-10 дозволяє бронювання тривалістю рівно 30 ночей"
        );
    }

    @Test
    @DisplayName("UT-14 | BL-30: ніч з п'ятниці на суботу має надбавку 15 %")
    void fridayNightHasWeekendSurcharge() {
        BigDecimal price = calc.nightPrice(
                new BigDecimal("100.00"),
                LocalDate.of(2026, 9, 18)
        );

        assertEquals(
                new BigDecimal("115.00"),
                price
        );
    }

    @ParameterizedTest(name = "BL-10: тривалість {0} ночей -> помилка меж: {1}")
    @CsvSource({
            "0, true",
            "1, false",
            "31, true"
    })
    @DisplayName("UT-15 | BL-10: граничні значення тривалості бронювання 0, 1 та 31 ніч")
    void bookingNightsBoundaryValues(int nights, boolean expectedError) {
        LocalDate checkIn = LocalDate.of(2026, 9, 1);
        LocalDate checkOut = checkIn.plusDays(nights);

        var errors = validator.validate(
                checkIn,
                checkOut,
                1,
                2,
                LocalDate.of(2026, 8, 20)
        );

        boolean actualError = nights == 0
                ? errors.contains(BookingError.CHECK_OUT_NOT_AFTER_CHECK_IN)
                : errors.contains(BookingError.NIGHTS_OUT_OF_RANGE);

        assertEquals(
                expectedError,
                actualError,
                "BL-10: має коректно обробляти межі тривалості бронювання"
        );
    }

    @ParameterizedTest(name = "BL-10: guests={0}, capacity=2 -> помилка: {1}")
    @CsvSource({
            "0, true",
            "1, false",
            "2, false",
            "3, true"
    })
    @DisplayName("UT-16 | BL-10: кількість гостей перевіряється на межах місткості номера")
    void guestCountBoundaryValues(int guests, boolean expectedError) {
        var errors = validator.validate(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 3),
                guests,
                2,
                LocalDate.of(2026, 8, 20)
        );

        assertEquals(
                expectedError,
                errors.contains(BookingError.GUESTS_OUT_OF_RANGE),
                "BL-10: кількість гостей має бути в межах від 1 до місткості номера"
        );
    }
}
