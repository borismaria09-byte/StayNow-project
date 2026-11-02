package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    @DisplayName("UT-01: Розрахунок вартості проживання з урахуванням сезонного коефіцієнта")
    void testUt01_calculateTotalPrice() {
        BigDecimal total = calc.total(new BigDecimal("150.00"),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13));
        assertEquals(new BigDecimal("360.00"), total);
    }

    @Test
    @DisplayName("UT-02: Перевірка помилки валідації, якщо дата виїзду передує даті заїзду")
    void testUt02_invalidDatesOrder() {
        var errors = validator.validate(
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 10),
                2, 2, LocalDate.of(2026, 11, 1), "guest@example.com", "+380501234567"
        );
        assertFalse(errors.isEmpty(), "Очікувалася помилка валідації через некоректний порядок дат");
    }

    @Test
    @DisplayName("UT-03: Перевірка помилки валідації при перевищенні кількості гостей")
    void testUt03_exceedMaxGuests() {
        var errors = validator.validate(
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12),
                10, 2, LocalDate.of(2026, 11, 1), "guest@example.com", "+380501234567"
        );
        assertFalse(errors.isEmpty(), "Очікувалася помилка валідації через перевищення кількості гостей");
    }

    @Test
    @DisplayName("UT-04: Перевірка помилки валідації при некоректному форматі email")
    void testUt04_invalidEmailFormat() {
        var errors = validator.validate(
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12),
                2, 2, LocalDate.of(2026, 11, 1), "invalid-email-address", "+380501234567"
        );
        assertFalse(errors.isEmpty(), "Очікувалася помилка валідації для некоректного email");
    }
}
