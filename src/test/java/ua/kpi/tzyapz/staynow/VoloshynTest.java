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
 * Закріплені ID: UT-09..UT-12 відповідно до docs/coverage-matrix.md.
 */
class VoloshynTest {

    @ParameterizedTest(name = "[{index}] {0}: guests={1}, isSuite={2} -> valid={3}")
    @DisplayName("UT-09 (BL-20): Валідація кількості гостей (граничні значення 0, 1, 4, 5, 6, 7)")
    @CsvSource({
            "Нуль гостей (невалідно), 0, false, false",
            "Мінімум 1 гість (валідно), 1, false, true",
            "Максимум 4 гості для стандартного номера (валідно), 4, false, true",
            "Перевищення ліміту 5 гостей для стандартного номера (невалідно), 5, false, false",
            "Максимум 6 гостей для Suite (валідно), 6, true, true",
            "Перевищення ліміту 7 гостей для Suite (невалідно), 7, true, false"
    })
    void ut09_validateGuestsBoundary(String caseName, int guests, boolean isSuite, boolean expectedValid) {
        LocalDate checkIn = LocalDate.of(2026, 5, 10);
        LocalDate checkOut = LocalDate.of(2026, 5, 12);

        List<BookingError> errors = BookingValidator.validate(checkIn, checkOut, guests, isSuite);

        if (expectedValid) {
            assertTrue(errors.isEmpty(),
                    () -> "Очікувалась успішна валідація (" + caseName + "), але отримано помилки: " + errors);
        } else {
            assertTrue(errors.contains(BookingError.INVALID_GUESTS),
                    () -> "Очікувалась помилка INVALID_GUESTS (" + caseName + "), але отримано: " + errors);
        }
    }

    @ParameterizedTest(name = "[{index}] {0}: baseRate={1}, checkIn={2}, nights={3} -> expected={4}")
    @DisplayName("UT-10 (BL-30): Сезонний коефіцієнт +25% у піковий період (червень-серпень)")
    @CsvSource({
            "Низький сезон (травень, будній день), 100.00, 2026-05-11, 1, 100.00",
            "Початок пікового сезону (1 червня, понеділок), 100.00, 2026-06-01, 1, 125.00",
            "Середина пікового сезону (15 липня, середа, 2 ночі), 100.00, 2026-07-15, 2, 250.00",
            "Кінець пікового сезону (31 серпня, понеділок), 100.00, 2026-08-31, 1, 125.00",
            "Після пікового сезону (1 вересня, вівторок), 100.00, 2026-09-01, 1, 100.00"
    })
    void ut10_peakSeasonSurcharge(String caseName, String baseRateStr, String checkInStr, int nights, String expectedStr) {
        BigDecimal baseRate = new BigDecimal(baseRateStr);
        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = checkIn.plusDays(nights);

        BigDecimal actual = PriceCalculator.calculateTotal(baseRate, checkIn, checkOut, 1, false);
        BigDecimal expected = new BigDecimal(expectedStr);

        assertEquals(expected, actual,
                () -> "Невірний розрахунок сезонного коефіцієнта (" + caseName + ")");
    }

    @ParameterizedTest(name = "[{index}] {0}: baseRate={1}, checkIn={2}, nights={3} -> expected={4}")
    @DisplayName("UT-11 (BL-40): Націнка +15% за проживання у вихідні дні (п'ятниця та субота)")
    @CsvSource({
            "Четвер у низький сезон (без націнки), 100.00, 2026-05-14, 1, 100.00",
            "П'ятниця у низький сезон (+15%), 100.00, 2026-05-15, 1, 115.00",
            "Субота у низький сезон (+15%), 100.00, 2026-05-16, 1, 115.00",
            "Неділя у низький сезон (без націнки), 100.00, 2026-05-17, 1, 100.00",
            "П'ятниця + субота у низький сезон (2 ночі по +15%), 100.00, 2026-05-15, 2, 230.00"
    })
    void ut11_weekendSurcharge(String caseName, String baseRateStr, String checkInStr, int nights, String expectedStr) {
        BigDecimal baseRate = new BigDecimal(baseRateStr);
        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = checkIn.plusDays(nights);

        BigDecimal actual = PriceCalculator.calculateTotal(baseRate, checkIn, checkOut, 1, false);
        BigDecimal expected = new BigDecimal(expectedStr);

        assertEquals(expected, actual,
                () -> "Невірний розрахунок вихідного тарифу (" + caseName + ")");
    }

    @ParameterizedTest(name = "[{index}] {0}: baseRate={1}, checkIn={2}, nights={3} -> expected={4}")
    @DisplayName("UT-12 (BL-70): Математичне округлення HALF_UP до 2 знаків після коми")
    @CsvSource({
            "0.005 округляється вгору (10.02 * 1.25 = 12.525 -> 12.53), 10.02, 2026-07-01, 1, 12.53",
            "0.0025 відкидається (10.01 * 1.25 = 12.5125 -> 12.51), 10.01, 2026-07-01, 1, 12.51",
            "0.0075 округляється вгору (10.03 * 1.25 = 12.5375 -> 12.54), 10.03, 2026-07-01, 1, 12.54",
            "Точне значення без дробового залишку (10.04 * 1.25 = 12.55), 10.04, 2026-07-01, 1, 12.55"
    })
    void ut12_roundingHalfUp(String caseName, String baseRateStr, String checkInStr, int nights, String expectedStr) {
        BigDecimal baseRate = new BigDecimal(baseRateStr);
        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = checkIn.plusDays(nights);

        BigDecimal actual = PriceCalculator.calculateTotal(baseRate, checkIn, checkOut, 1, false);
        BigDecimal expected = new BigDecimal(expectedStr);

        assertEquals(expected, actual,
                () -> "Помилка округлення HALF_UP (" + caseName + ")");
    }
}