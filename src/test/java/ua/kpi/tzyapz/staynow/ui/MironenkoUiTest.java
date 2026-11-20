package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("Sprint 4: UI-тест UI-03 (Виконавець: Міроненко Микита)")
class MironenkoUiTest {

    private static final String BASE_URL =
            "https://automationintesting.online/";

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeEach
    void setUp() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--headless=new",
                "--window-size=1280,900",
                "--remote-allow-origins=*"
        );

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(25)
        );

        js = (JavascriptExecutor) driver;

        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    private List<WebElement> getDateInputs() {

        return wait.until(driver -> {

            List<WebElement> inputs =
                    driver.findElements(
                            By.cssSelector(
                                    "#booking input.form-control"
                            )
                    );

            List<WebElement> visible =
                    inputs.stream()
                            .filter(WebElement::isDisplayed)
                            .toList();

            return visible.size() >= 2
                    ? visible
                    : null;
        });
    }

    private void setDate(
            WebElement input,
            String date
    ) {

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                input
        );

        js.executeScript(
                "arguments[0].click();",
                input
        );

        input.sendKeys(
                Keys.chord(
                        Keys.COMMAND,
                        "a"
                )
        );

        input.sendKeys(date);
        input.sendKeys(Keys.TAB);
    }

    /*
     * Створюємо передумову для TC-003.
     *
     * Через особливість тестового сайту кімната,
     * отримана через /api/room, іноді все одно
     * повертає 409 при POST /api/booking.
     *
     * Тому перебираємо кілька кімнат і дат,
     * доки бронювання реально не буде створено.
     */
    private String createAvailableBooking() {

        Object result = js.executeAsyncScript(
                """
                const done = arguments[arguments.length - 1];

                (async () => {

                    function formatDate(date) {
                        return date.toISOString().split('T')[0];
                    }

                    const start = new Date();

                    /*
                     * Перевіряємо до 12 різних
                     * майбутніх інтервалів.
                     */
                    for (let attempt = 0; attempt < 12; attempt++) {

                        const checkIn = new Date(start);

                        checkIn.setUTCDate(
                            checkIn.getUTCDate()
                            + 120
                            + attempt * 10
                        );

                        const checkOut = new Date(checkIn);

                        checkOut.setUTCDate(
                            checkOut.getUTCDate() + 2
                        );

                        const checkInText =
                            formatDate(checkIn);

                        const checkOutText =
                            formatDate(checkOut);

                        try {

                            const roomResponse =
                                await fetch(
                                    '/api/room?checkin='
                                    + checkInText
                                    + '&checkout='
                                    + checkOutText
                                );

                            const roomData =
                                await roomResponse.json();

                            const rooms =
                                roomData.rooms || [];

                            /*
                             * Перевіряємо всі кімнати,
                             * які endpoint назвав доступними.
                             */
                            for (const room of rooms) {

                                const booking = {
                                    roomid: room.roomid,
                                    firstname: 'T3-Test',
                                    lastname: 'T3-Testenko',
                                    depositpaid: false,

                                    bookingdates: {
                                        checkin: checkInText,
                                        checkout: checkOutText
                                    },

                                    email: 't3@example.com',
                                    phone: '+380001234567'
                                };

                                const response =
                                    await fetch(
                                        '/api/booking',
                                        {
                                            method: 'POST',

                                            headers: {
                                                'Content-Type':
                                                    'application/json'
                                            },

                                            body:
                                                JSON.stringify(
                                                    booking
                                                )
                                        }
                                    );

                                /*
                                 * 201 = бронювання
                                 * реально створене.
                                 */
                                if (response.status === 201) {

                                    done(
                                        'OK|'
                                        + room.roomid
                                        + '|'
                                        + checkInText
                                        + '|'
                                        + checkOutText
                                    );

                                    return;
                                }
                            }

                        } catch (error) {
                            // пробуємо наступний інтервал
                        }
                    }

                    done('NO_BOOKING');

                })();
                """
        );

        return String.valueOf(result);
    }

    @Test
    @DisplayName(
            "UI-03 / TC-003 / F-30: заброньований номер не відображається серед доступних на ті самі дати"
    )
    void bookedRoomIsFilteredFromAvailableRooms() {

        /*
         * 1. Створюємо реальне бронювання.
         */
        String bookingResult =
                createAvailableBooking();

        assertTrue(
                bookingResult.startsWith("OK|"),
                "Передумова TC-003: бронювання має бути створене. "
                        + "Результат: "
                        + bookingResult
        );

        /*
         * Формат:
         *
         * OK|roomId|checkIn|checkOut
         */
        String[] parts =
                bookingResult.split("\\|");

        final String bookedRoomId =
                parts[1];

        LocalDate checkIn =
                LocalDate.parse(
                        parts[2]
                );

        LocalDate checkOut =
                LocalDate.parse(
                        parts[3]
                );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        String checkInUi =
                checkIn.format(formatter);

        String checkOutUi =
                checkOut.format(formatter);

        /*
         * 2. Відкриваємо головну сторінку.
         */
        driver.get(BASE_URL);

        /*
         * 3. Знаходимо поля Check In / Check Out.
         */
        List<WebElement> inputs =
                getDateInputs();

        WebElement checkInInput =
                inputs.get(0);

        setDate(
                checkInInput,
                checkInUi
        );

        /*
         * React може перемалювати DOM.
         */
        inputs =
                getDateInputs();

        WebElement checkOutInput =
                inputs.get(1);

        setDate(
                checkOutInput,
                checkOutUi
        );

        /*
         * 4. Натискаємо Check Availability.
         */
        WebElement checkAvailability =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath(
                                        "//button[normalize-space()='Check Availability']"
                                )
                        )
                );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                checkAvailability
        );

        js.executeScript(
                "arguments[0].click();",
                checkAvailability
        );

        /*
         * 5. Чекаємо, доки заброньований номер
         * зникне зі списку доступних.
         */
        wait.until(driver -> {

            List<WebElement> links =
                    driver.findElements(
                            By.cssSelector(
                                    ".room-card a[href*='/reservation/']"
                            )
                    );

            return links.stream()
                    .filter(WebElement::isDisplayed)
                    .noneMatch(link -> {

                        String href =
                                link.getAttribute("href");

                        return href != null
                                && href.contains(
                                "/reservation/"
                                        + bookedRoomId
                        );
                    });
        });

        /*
         * 6. Фінальна перевірка TC-003 / F-30.
         */
        List<WebElement> availableRooms =
                driver.findElements(
                        By.cssSelector(
                                ".room-card a[href*='/reservation/']"
                        )
                );

        boolean bookedRoomStillVisible =
                availableRooms.stream()
                        .filter(WebElement::isDisplayed)
                        .anyMatch(link -> {

                            String href =
                                    link.getAttribute("href");

                            return href != null
                                    && href.contains(
                                    "/reservation/"
                                            + bookedRoomId
                            );
                        });

        assertFalse(
                bookedRoomStillVisible,
                "TC-003 / F-30: заброньований номер "
                        + bookedRoomId
                        + " не повинен відображатися "
                        + "серед доступних на ті самі дати"
        );
    }
}
