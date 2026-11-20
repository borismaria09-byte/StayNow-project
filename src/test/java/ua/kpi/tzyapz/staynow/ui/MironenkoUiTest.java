package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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

        ChromeOptions options =
                new ChromeOptions();

        options.addArguments(
                "--headless=new",
                "--window-size=1280,900",
                "--remote-allow-origins=*"
        );

        driver =
                new ChromeDriver(options);

        driver.manage()
                .timeouts()
                .scriptTimeout(
                        Duration.ofSeconds(60)
                );

        wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(30)
                );

        js =
                (JavascriptExecutor) driver;

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
                """
                const element = arguments[0];
                const value = arguments[1];

                const setter =
                    Object.getOwnPropertyDescriptor(
                        HTMLInputElement.prototype,
                        'value'
                    ).set;

                setter.call(element, value);

                element.dispatchEvent(
                    new Event('input', { bubbles: true })
                );

                element.dispatchEvent(
                    new Event('change', { bubbles: true })
                );
                """,
                input,
                date
        );
    }

    private String createAvailableBooking() {

        Object result =
                js.executeAsyncScript(
                        """
                        const done =
                            arguments[
                                arguments.length - 1
                            ];

                        (async () => {

                            function formatDate(date) {
                                return date
                                    .toISOString()
                                    .split('T')[0];
                            }

                            const start =
                                new Date();

                            for (
                                let attempt = 0;
                                attempt < 12;
                                attempt++
                            ) {

                                const checkIn =
                                    new Date(start);

                                checkIn.setUTCDate(
                                    checkIn.getUTCDate()
                                    + 120
                                    + attempt * 10
                                );

                                const checkOut =
                                    new Date(checkIn);

                                checkOut.setUTCDate(
                                    checkOut.getUTCDate()
                                    + 2
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

                                    for (
                                        const room
                                        of rooms
                                    ) {

                                        const booking = {
                                            roomid:
                                                room.roomid,

                                            firstname:
                                                'T3-Test',

                                            lastname:
                                                'T3-Testenko',

                                            depositpaid:
                                                false,

                                            bookingdates: {
                                                checkin:
                                                    checkInText,

                                                checkout:
                                                    checkOutText
                                            },

                                            email:
                                                't3@example.com',

                                            phone:
                                                '+380001234567'
                                        };

                                        const response =
                                            await fetch(
                                                '/api/booking',
                                                {
                                                    method:
                                                        'POST',

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

                                        if (
                                            response.status
                                            === 201
                                        ) {

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

        String bookingResult =
                createAvailableBooking();

        assertTrue(
                bookingResult.startsWith("OK|"),
                "Передумова TC-003: бронювання має бути створене. "
                        + "Результат: "
                        + bookingResult
        );

        String[] parts =
                bookingResult.split("\\|");

        final String bookedRoomId =
                parts[1];

        final String checkIn =
                parts[2];

        final String checkOut =
                parts[3];

        driver.get(BASE_URL);

        List<WebElement> inputs =
                getDateInputs();

        WebElement checkInInput =
                inputs.get(0);

        setDate(
                checkInInput,
                checkIn
        );

        inputs =
                getDateInputs();

        WebElement checkOutInput =
                inputs.get(1);

        setDate(
                checkOutInput,
                checkOut
        );

        wait.until(driver -> {

            List<WebElement> currentInputs =
                    getDateInputs();

            String actualCheckIn =
                    currentInputs.get(0)
                            .getAttribute("value");

            String actualCheckOut =
                    currentInputs.get(1)
                            .getAttribute("value");

            return checkIn.equals(actualCheckIn)
                    && checkOut.equals(actualCheckOut);
        });

        WebElement checkAvailability =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath(
                                        "//button[normalize-space()='Check Availability']"
                                )
                        )
                );

        js.executeScript(
                "arguments[0].click();",
                checkAvailability
        );

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
