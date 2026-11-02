package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("СтейНау — юніт-тести Борис Марія")
class BorysTest {

    private BookingValidator validator;
    private PriceCalculator calc;
    private Availability availability;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();
        calc = new PriceCalculator();
        availability = new Availability();
    }

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

        assertTrue(
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

    @Test
    @DisplayName("UT-15 | BL-60: скасування рівно за 7 днів безкоштовне")
    void cancellationExactlySevenDaysBeforeIsFree() {
        BigDecimal fee = availability.cancellationFee(
                new BigDecimal("1000.00"),
                7
        );

        assertEquals(
                new BigDecimal("0.00"),
                fee
        );
    }

    @Test
    @DisplayName("UT-16 | BL-70: заїзд у день виїзду попереднього гостя дозволено")
    void checkInOnPreviousCheckOutDateIsAvailable() {
        availability.book(
                "Single",
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12)
        );

        boolean available = availability.isAvailable(
                "Single",
                LocalDate.of(2026, 11, 12),
                LocalDate.of(2026, 11, 14)
        );

        assertTrue(
                available,
                "BL-70 дозволяє заїзд у день виїзду попереднього гостя"
        );
    }
}
