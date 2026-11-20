package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("ui")
@DisplayName("Sprint 4: UI-тести UI-01–UI-02 (Виконавець: Проценко Д.-С.)")
class ProtsenkoUiTest {

    private static final String BASE_URL =
            "https://automationintesting.online/";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--window-size=1280,900",
                "--remote-allow-origins=*"
        );

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void openBookingPage() {

        Object result =
                ((JavascriptExecutor) driver).executeAsyncScript(
                        "const done = arguments[arguments.length - 1];" +
                        "fetch('/api/room?checkin=2026-11-20&checkout=2026-11-22')" +
                        ".then(r => r.json())" +
                        ".then(data => {" +
                        "  const rooms = data.rooms || [];" +
                        "  done(rooms.map(r => r.roomid).join(','));" +
                        "})" +
                        ".catch(() => done(''));"
                );

        String roomIds = result == null
                ? ""
                : result.toString();

        if (roomIds.isBlank()) {
            fail("Не знайдено доступних номерів");
        }

        for (String roomId : roomIds.split(",")) {

            driver.get(
                    BASE_URL
                            + "reservation/"
                            + roomId
                            + "?checkin=2026-11-20"
                            + "&checkout=2026-11-22"
            );

            try {
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(5)
                ).until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".booking-card")
                        )
                );

                return;

            } catch (TimeoutException ignored) {
            }
        }

        fail("Не вдалося відкрити сторінку бронювання жодного доступного номера");
    }

    @Test
    @DisplayName("UI-01 / TC-012: Перевірка Price Summary та розрахунку Cleaning fee, Service fee і Total")
    void testUi01_bookingFormCalculation() {

        openBookingPage();

        WebElement priceSummaryBlock = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//h3[contains(normalize-space(),'Price Summary')]/parent::*"
                        )
                )
        );

        String summaryText =
                priceSummaryBlock.getText();

        assertAll(
                "Перевірка блоку Price Summary (TC-012 / GUI-50)",
                () -> assertTrue(
                        summaryText.contains("nights"),
                        "У Price Summary має відображатися кількість ночей та базовий тариф"
                ),
                () -> assertTrue(
                        summaryText.contains("Cleaning fee")
                                && summaryText.contains("£25"),
                        "Має відображатися Cleaning fee £25"
                ),
                () -> assertTrue(
                        summaryText.contains("Service fee")
                                && summaryText.contains("£15"),
                        "Має відображатися Service fee £15"
                ),
                () -> assertTrue(
                        summaryText.contains("Total"),
                        "Має відображатися підсумковий рядок Total"
                )
        );
    }

    @Test
    @DisplayName("UI-02 / TC-017: Спроба бронювання номера з порожніми полями форми бронювання")
    void testUi02_validationErrorMessageDisplay() {

        openBookingPage();

        WebElement openFormButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("doReservation")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                openFormButton
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("firstname")
                )
        );

        WebElement submitBookingButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(
                                "//button[normalize-space()='Reserve Now']"
                        )
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                submitBookingButton
        );

        WebElement errorAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".alert-danger")
                )
        );

        assertAll(
                "Перевірка помилок валідації при порожніх полях (TC-017 / GUI-80)",
                () -> assertTrue(
                        errorAlert.isDisplayed(),
                        "Блок повідомлень про помилки валідації має відображатися"
                ),
                () -> assertFalse(
                        errorAlert.getText().isBlank(),
                        "Текст повідомлення про помилку не має бути порожнім"
                ),
                () -> assertFalse(
                        driver.getPageSource()
                                .contains("Booking Confirmed"),
                        "Підтвердження бронювання не повинно з'являтися при порожніх полях"
                )
        );
    }
}
