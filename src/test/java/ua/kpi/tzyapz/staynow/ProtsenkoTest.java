package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("Sprint 4: Юніт-тести UT-01–UT-04 (Виконавець: Проценко Д.-С.)")
class ProtsenkoTest {

    private PriceCalculator calc;
    private BookingValidator validator;

    @BeforeEach
    void setUp() {
        calc = new PriceCalculator();
        validator = new BookingValidator();
    }

    @Test
    @DisplayName("UT-01 | BL-50: розрахунок вартості проживання у низький сезон (BL-40) повертає коректну суму")
    void testUt01_calculateTotalPrice() {
        BigDecimal total = calc.total(new BigDecimal("150.00"),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13));
        assertEquals(new BigDecimal("360.00"), total);
    }

    @Test
    @DisplayName("UT-02 | BL-10: дата виїзду раніша за дату заїзду повертає помилку")
    void testUt02_invalidDatesOrder() {
        var errors = validator.validate(
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 10),
                2, 2, LocalDate.of(2026, 11, 1), "guest@example.com", "+380501234567"
        );
        assertTrue(errors.contains(BookingError.CHECK_OUT_NOT_AFTER_CHECK_IN),
                "Очікувалася помилка CHECK_OUT_NOT_AFTER_CHECK_IN через некоректний порядок дат");
    }

    @Test
    @DisplayName("UT-03 | BL-10: кількість гостей більша за місткість номера повертає помилку")
    void testUt03_exceedMaxGuests() {
        var errors = validator.validate(
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12),
                10, 2, LocalDate.of(2026, 11, 1), "guest@example.com", "+380501234567"
        );
        assertTrue(errors.contains(BookingError.GUESTS_OUT_OF_RANGE),
                "Очікувалася помилка GUESTS_OUT_OF_RANGE через перевищення кількості гостей");
    }

    @Test
    @DisplayName("UT-04 | BL-20: некоректний формат email повертає помилку")
    void testUt04_invalidEmailFormat() {
        var errors = validator.validate(
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12),
                2, 2, LocalDate.of(2026, 11, 1), "invalid-email-address", "+380501234567"
        );
        assertTrue(errors.contains(BookingError.INVALID_EMAIL),
                "Очікувалася помилка INVALID_EMAIL для некоректного email");
    }
}
