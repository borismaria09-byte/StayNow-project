package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("Sprint 4: Юніт-тести (Виконавець: Міроненко Микита)")
class MironenkoTest {

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
    @DisplayName("UT-21 | BL-10: дата виїзду раніша за дату заїзду повертає помилку")
    void validate_CheckOutBeforeCheckIn_ReturnsError() {
        LocalDate today = LocalDate.of(2026, 11, 10);
        LocalDate checkIn = LocalDate.of(2026, 11, 15);
        LocalDate checkOut = LocalDate.of(2026, 11, 14); 

        List<BookingError> errors = validator.validate(checkIn, checkOut, 2, 2, today);

        assertTrue(errors.contains(BookingError.CHECK_OUT_NOT_AFTER_CHECK_IN), "Має бути помилка дат");
    }

    @ParameterizedTest(name = "BL-20: невалідний email {0} повертає false")
    @ValueSource(strings = {"t3.testexample.com", "t3.test@example", ""})
    @DisplayName("UT-22 | BL-20: валідація невалідного email")
    void isValidEmail_InvalidEmail_ReturnsFalse(String email) {
        assertFalse(validator.isValidEmail(email));
    }

    @ParameterizedTest(name = "BL-30: дата {0} є вихідною -> {1}")
    @CsvSource({
            "2026-11-14, true", // Субота
            "2026-11-15, true", // Неділя
            "2026-11-13, false" // П'ятниця
    })
    @DisplayName("UT-23 | BL-30: перевірка вихідних днів для надбавки")
    value_isWeekendNight(String dateStr, boolean expected) {
        LocalDate date = LocalDate.parse(dateStr);
        assertEquals(expected, calc.isWeekendNight(date));
    }

    @Test
    @DisplayName("UT-24 | BL-60: скасування за 7 днів - безкоштовно (0%)")
    void cancellationFee_7Days_IsFree() {
        BigDecimal total = new BigDecimal("1000.00");
        BigDecimal fee = availability.cancellationFee(total, 7);
        
        assertEquals(new BigDecimal("0.00"), fee);
    }
}