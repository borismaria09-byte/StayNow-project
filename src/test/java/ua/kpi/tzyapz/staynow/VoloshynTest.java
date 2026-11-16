package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.AfterEach;
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
@DisplayName("Юніт-тести UT-09..UT-12 (Волошин Дмитро)")
class VoloshynTest {

    private PriceCalculator priceCalculator;
    private Availability availability;
    private BookingValidator bookingValidator;

    @BeforeEach
    void setUp() {
        priceCalculator = new PriceCalculator();
        availability = new Availability();
        bookingValidator = new BookingValidator();
    }

    @AfterEach
    void tearDown() {
        priceCalculator = null;
        availability = null;
        bookingValidator = null;
    }

    @ParameterizedTest(name = "UT-09 [BL-30/BL-40]: дата {0} (база {1}) -> ціна ночі {2}")
    @CsvSource({
            "2026-05-28, 100.00, 100.00", // четвер, стандартний сезон (1.00)
            "2026-06-01, 100.00, 130.00", // понеділок, старт літнього високого сезону (1.30)
            "2026-08-31, 100.00, 130.00", // понеділок, кінець літнього високого сезону (1.30)
            "2026-09-01, 100.00, 100.00", // вівторок, межа після високого сезону (1.00)
            "2026-02-03, 100.00, 80.00",  // вівторок, низький сезон (0.80)
            "2026-06-05, 133.33, 199.33"  // п'ятниця (вихідна ніч 1.15) + високий сезон (1.30) + HALF_UP
    })
    @DisplayName("UT-09 [BL-30/BL-40]: сезонні коефіцієнти, вихідна ніч (пт/сб) та округлення HALF_UP")
    void ut09_nightPriceSeasonWeekendAndRounding(String dateStr, String baseRateStr, String expectedStr) {
        BigDecimal baseRate = new BigDecimal(baseRateStr);
        LocalDate night = LocalDate.parse(dateStr);

        BigDecimal actual = priceCalculator.nightPrice(baseRate, night);

        assertEquals(new BigDecimal(expectedStr), actual);
    }

    @ParameterizedTest(name = "UT-10 [BL-50]: {0} ночей від 2026-04-06 -> підсумок {1}")
    @CsvSource({
            "6, 630.00",  // 4 будні*100 + 2 вихідних (пт, сб)*115 = 630.00 (< 7 ночей, без знижки)
            "7, 657.00",  // (5 буднів*100 + 2 вихідних*115) * 0.90 = 730 * 0.90 = 657.00 (межа 7 ночей, -10%)
            "8, 747.00"   // (6 буднів*100 + 2 вихідних*115) * 0.90 = 830 * 0.90 = 747.00 (> 7 ночей, -10%)
    })
    @DisplayName("UT-10 [BL-50]: знижка 10% за тривалість проживання від 7 ночей")
    void ut10_totalStayDiscountAtSevenNightsBoundary(int nights, String expectedTotal) {
        LocalDate checkIn = LocalDate.of(2026, 4, 6); // понеділок
        LocalDate checkOut = checkIn.plusDays(nights);
        BigDecimal baseRate = new BigDecimal("100.00");

        BigDecimal actual = priceCalculator.total(baseRate, checkIn, checkOut);

        assertEquals(new BigDecimal(expectedTotal), actual);
    }

    @ParameterizedTest(name = "UT-11 [BL-60]: скасування за {0} дн. -> штраф {1}, повернення {2}")
    @CsvSource({
            "8, 0.00, 1000.00",    // > 7 днів -> 0% штраф
            "7, 0.00, 1000.00",    // межа 7 днів -> 0% штраф
            "6, 500.00, 500.00",   // межа 6 днів -> 50% штраф
            "3, 500.00, 500.00",   // межа 3 дні -> 50% штраф
            "2, 1000.00, 0.00",    // межа 2 дні (< 3) -> 100% штраф
            "0, 1000.00, 0.00"     // день заїзду -> 100% штраф
    })
    @DisplayName("UT-11 [BL-60]: плата за скасування та сума повернення на межах 7 та 3 днів")
    void ut11_cancellationFeeAndRefundAtBoundaries(int daysBefore, String expectedFee, String expectedRefund) {
        BigDecimal total = new BigDecimal("1000.00");

        assertAll("Скасування за " + daysBefore + " днів",
                () -> assertEquals(new BigDecimal(expectedFee), availability.cancellationFee(total, daysBefore)),
                () -> assertEquals(new BigDecimal(expectedRefund), availability.refund(total, daysBefore))
        );
    }

    @Test
    @DisplayName("UT-12 [BL-10/BL-20/BL-70]: валідація межі 30 ночей, дати у минулому та стикування бронювань")
    void ut12_bookingValidationAndRoomAvailabilityBoundaries() {
        LocalDate today = LocalDate.of(2026, 5, 10);

        List<BookingError> errorsAtMaxNights = bookingValidator.validate(
                today, today.plusDays(30), 2, 2, today, "dima@kpi.ua", "+380991234567");

        List<BookingError> errorsPastCheckIn = bookingValidator.validate(
                today.minusDays(1), today.plusDays(2), 2, 2, today);

        availability.book("101", LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12));
        boolean backToBackAvailable = availability.isAvailable(
                "101", LocalDate.of(2026, 5, 12), LocalDate.of(2026, 5, 15));

        assertAll("Граничні правила валідації та доступності",
                () -> assertTrue(errorsAtMaxNights.isEmpty(),
                        "30 ночей мають бути дозволені (MAX_NIGHTS = 30), але отримано: " + errorsAtMaxNights),
                () -> assertTrue(errorsPastCheckIn.contains(BookingError.CHECK_IN_IN_PAST),
                        "Заїзд у минулому має повертати CHECK_IN_IN_PAST"),
                () -> assertTrue(backToBackAvailable,
                        "Заїзд нового гостя у день виїзду попереднього має бути дозволений")
        );
    }

    @ParameterizedTest(name = "UT-12b [BL-20]: невалідний email={0}")
    @ValueSource(strings = {"", "plainaddress", "user@", "@domain.com"})
    @DisplayName("UT-12b [BL-20]: відхилення некоректних email та телефону (не +380XXXXXXXXX)")
    void ut12b_invalidContactDataReturnsErrors(String badEmail) {
        LocalDate today = LocalDate.of(2026, 5, 10);

        List<BookingError> errors = bookingValidator.validate(
                today, today.plusDays(2), 1, 2, today, badEmail, "0991234567");

        assertAll("Помилки контактних даних",
                () -> assertTrue(errors.contains(BookingError.INVALID_EMAIL)),
                () -> assertTrue(errors.contains(BookingError.INVALID_PHONE))
        );
    }
}