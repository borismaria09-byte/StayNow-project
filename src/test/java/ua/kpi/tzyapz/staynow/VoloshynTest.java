package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести бізнес-логіки модуля StayNow v1 (Волошин Дмитро, група ФБ-41).
 * Відповідність docs/coverage-matrix.md:
 * - UT-09 -> BL-20 (валідація email та телефону +380...)
 * - UT-10 -> BL-50 (знижка 10% від 7 ночей)
 * - UT-11 -> BL-60 (штраф за скасування: 7 / 6 / 3 / 2 дні)
 * - UT-12 -> BL-70 (перевірка перетину дат бронювання)
 */
class VoloshynTest {

    @ParameterizedTest(name = "[{index}] {0}: email=''{1}'', phone=''{2}'' -> valid={3}")
    @DisplayName("UT-09 (BL-20): Валідація email та телефону формату +380...")
    @CsvSource({
            "Порожній email, '', +380501234567, false",
            "Email без @ (plainaddress), plainaddress, +380501234567, false",
            "Email без домену (user@), user@, +380501234567, false",
            "Некоректний телефон без +380, user@example.com, 0501234567, false",
            "Коректні email та телефон +380, user@example.com, +380501234567, true"
    })
    void ut09_validateContactInfo(String caseName, String email, String phone, boolean expectedValid) {
        BookingValidator validator = new BookingValidator();
        LocalDate today = LocalDate.of(2026, 11, 1);
        LocalDate checkIn = LocalDate.of(2026, 11, 10);
        LocalDate checkOut = LocalDate.of(2026, 11, 12);

        List<BookingError> errors = validator.validate(
                checkIn, checkOut, 2, 4, today, email, phone
        );

        if (expectedValid) {
            assertTrue(errors.isEmpty(),
                    () -> "Очікувалась валідна контактна інформація (" + caseName + "), але отримано: " + errors);
        } else {
            boolean hasContactError = errors.contains(BookingError.INVALID_EMAIL)
                    || errors.contains(BookingError.INVALID_PHONE);
            assertTrue(hasContactError,
                    () -> "Очікувалась помилка email або телефону (" + caseName + "), але отримано: " + errors);
        }
    }

    @ParameterizedTest(name = "[{index}] {0}: nights={2} -> expected={3}")
    @DisplayName("UT-10 (BL-50): Знижка 10% при бронюванні від 7 ночей (межа 6 vs 7 ночей)")
    @CsvSource({
            "6 ночей — без знижки (0%), 100.00, 6, 630.00",
            "7 ночей — знижка 10%, 100.00, 7, 657.00",
            "10 ночей — знижка 10%, 100.00, 10, 927.00"
    })
    void ut10_longStayDiscount(String caseName, String baseRateStr, int nights, String expectedStr) {
        PriceCalculator calculator = new PriceCalculator();
        BigDecimal baseRate = new BigDecimal(baseRateStr);
        LocalDate checkIn = LocalDate.of(2026, 4, 6);
        LocalDate checkOut = checkIn.plusDays(nights);

        BigDecimal actual = calculator.total(baseRate, checkIn, checkOut);
        BigDecimal expected = new BigDecimal(expectedStr);

        assertEquals(expected, actual,
                () -> "Невірний розрахунок знижки 10% за тривалість проживання (" + caseName + ")");
    }

    @ParameterizedTest(name = "[{index}] {0}: daysBefore={1} -> expectedFee={3}")
    @DisplayName("UT-11 (BL-60): Штраф за скасування бронювання (7 днів -> 0%, 6/3 дні -> 50%, 2 дні -> 100%)")
    @CsvSource({
            "7 днів до заїзду (штраф 0%), 7, 1000.00, 0.00",
            "6 днів до заїзду (штраф 50%), 6, 1000.00, 500.00",
            "3 дні до заїзду (штраф 50%), 3, 1000.00, 500.00",
            "2 дні до заїзду (штраф 100%), 2, 1000.00, 1000.00"
    })
    void ut11_cancellationFee(String caseName, int daysBeforeCheckIn, String totalAmountStr, String expectedFeeStr) {
        Availability availability = new Availability();
        BigDecimal totalAmount = new BigDecimal(totalAmountStr);

        BigDecimal actualFee = availability.cancellationFee(totalAmount, daysBeforeCheckIn);
        BigDecimal expectedFee = new BigDecimal(expectedFeeStr);

        assertEquals(expectedFee, actualFee,
                () -> "Невірний розрахунок штрафу за скасування (" + caseName + ")");
    }

    @ParameterizedTest(name = "[{index}] {0}: [{1}..{2}] -> available={3}")
    @DisplayName("UT-12 (BL-70): Перевірка перетину дат бронювань (існуюче 10.11–12.11)")
    @CsvSource({
            "Стикове заселення в день виїзду 12.11–14.11 (дозволено), 2026-11-12, 2026-11-14, true",
            "Стиковий виїзд у день заїзду 08.11–10.11 (дозволено), 2026-11-08, 2026-11-10, true",
            "Частковий перетин справа 11.11–13.11 (відхилено), 2026-11-11, 2026-11-13, false",
            "Частковий перетин зліва 09.11–11.11 (відхилено), 2026-11-09, 2026-11-11, false",
            "Повний збіг дат 10.11–12.11 (відхилено), 2026-11-10, 2026-11-12, false"
    })
    void ut12_bookingDateOverlap(String caseName, String newCheckInStr, String newCheckOutStr, boolean expectedAvailable) {
        String roomId = "ROOM-101";
        Availability availability = new Availability();
        availability.book(roomId, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));

        LocalDate newCheckIn = LocalDate.parse(newCheckInStr);
        LocalDate newCheckOut = LocalDate.parse(newCheckOutStr);

        boolean actualAvailable = availability.isAvailable(roomId, newCheckIn, newCheckOut);

        assertEquals(expectedAvailable, actualAvailable,
                () -> "Невірна перевірка перетину дат бронювання (" + caseName + ")");
    }
}