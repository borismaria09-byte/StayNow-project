package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("СтейНау — юніт-тести UT-17–UT-20 (Михайленко Ростислав")
class MykhailenkoTest {

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
    @DisplayName("UT-17 | BL-10: дата заїзду раніша за сьогодні — помилка CHECK_IN_IN_PAST")
    void checkInInThePastIsRejected() {

        LocalDate today = LocalDate.of(2026, 11, 4);
        LocalDate checkIn = LocalDate.of(2026, 11, 3);
        LocalDate checkOut = LocalDate.of(2026, 11, 10);

        var errors = validator.validate(checkIn, checkOut, 1, 2, today);

        assertTrue(errors.contains(BookingError.CHECK_IN_IN_PAST),
                "BL-10: дата заїзду не може бути раніше за поточну дату; отримано " + errors);
    }

    @ParameterizedTest(name = "BL-40: {0} → коефіцієнт {1}")
    @CsvSource({
            "2026-05-31, 1.00",
            "2026-06-01, 1.30",
            "2026-08-31, 1.30",
            "2026-09-01, 1.00",
            "2026-12-19, 1.00",
            "2026-12-20, 1.30",
            "2027-01-10, 1.30",
            "2027-01-11, 1.00",
            "2027-01-31, 1.00",
            "2027-02-01, 0.80",
            "2027-03-31, 0.80",
            "2027-04-01, 1.00",
            "2026-10-31, 1.00",
            "2026-11-01, 0.80",
            "2026-11-30, 0.80",
            "2026-12-01, 1.00"
    })
    @DisplayName("UT-18 | BL-40: сезонний коефіцієнт на межах сезонів")
    void seasonFactorAtSeasonBoundaries(String date, String expectedFactor) {
        BigDecimal factor = calc.seasonFactor(LocalDate.parse(date));

        assertEquals(new BigDecimal(expectedFactor), factor);
    }

    @Test
    @DisplayName("UT-19 | BL-50: вартість ночі округлюється HALF_UP (43.329 → 43.33)")
    void nightPriceIsRoundedHalfUp() {

        BigDecimal baseRate = new BigDecimal("33.33");
        LocalDate wednesdayInHighSeason = LocalDate.of(2026, 7, 1);

        BigDecimal price = calc.nightPrice(baseRate, wednesdayInHighSeason);

        assertEquals(new BigDecimal("43.33"), price);
    }

    @Test
    @DisplayName("UT-20 | BL-70: бронювання на зайнятий інтервал — IllegalStateException, номер лишається зайнятим")
    void bookingOverlappingStayThrows() {
        availability.book("Single", LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 14));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> availability.book("Single", LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 16)));

        assertAll("Наслідки відмови",
                () -> assertNotNull(ex.getMessage()),
                () -> assertFalse(availability.isAvailable("Single",
                        LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 13)),
                        "Номер має лишатися зайнятим"),
                () -> assertEquals(1, availability.stays().size(),
                        "Відхилене бронювання не має реєструватися"),
                () -> assertTrue(availability.isAvailable("Double",
                        LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 16)),
                        "Інший номер на ці дати вільний")
        );
    }
}
