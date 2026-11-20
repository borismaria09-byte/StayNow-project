package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
@DisplayName("Sprint 4: UI-тести UI-05–UI-06 (Виконавець: Борис Марія)")
class BorysUiTest {

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

    private Keys getModifierKey() {

        Platform platform =
                ((org.openqa.selenium.HasCapabilities) driver)
                        .getCapabilities()
                        .getPlatformName();

        if (platform != null
                && platform.toString()
                .toLowerCase()
                .contains("mac")) {

            return Keys.COMMAND;
        }

        return Keys.CONTROL;
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

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        input
                )
        );

        input.click();

        input.sendKeys(
                Keys.chord(
                        getModifierKey(),
                        "a"
                )
        );

        input.sendKeys(
                Keys.BACK_SPACE
        );

        input.sendKeys(date);

        input.sendKeys(
                Keys.TAB
        );
    }

    @Test
    @DisplayName(
            "UI-05 / TC-005 / GUI-10: шапка залишається видимою під час прокручування"
    )
    void testUi05_headerRemainsVisible() {

        WebElement header = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("nav")
                )
        );

        assertAll(
                "Елементи шапки",
                () -> assertTrue(
                        header.getText()
                                .contains("Shady Meadows B&B")
                ),
                () -> assertTrue(
                        header.getText()
                                .contains("Rooms")
                ),
                () -> assertTrue(
                        header.getText()
                                .contains("Booking")
                ),
                () -> assertTrue(
                        header.getText()
                                .contains("Amenities")
                ),
                () -> assertTrue(
                        header.getText()
                                .contains("Location")
                ),
                () -> assertTrue(
                        header.getText()
                                .contains("Contact")
                ),
                () -> assertTrue(
                        header.getText()
                                .contains("Admin")
                )
        );

        WebElement contact = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//*[contains(text(),'Send Us a Message')]"
                        )
                )
        );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                contact
        );

        Boolean headerVisible =
                (Boolean) js.executeScript(
                        """
                        const r =
                            arguments[0]
                                .getBoundingClientRect();

                        return r.bottom > 0
                            && r.top < window.innerHeight;
                        """,
                        header
                );

        assertTrue(
                headerVisible,
                "Шапка має залишатися видимою після прокручування сторінки"
        );
    }

    @Test
    @DisplayName(
            "UI-06 / TC-002 / F-20: картка Single містить дані номера та відкриває сторінку бронювання"
    )
    void testUi06_singleRoomCardAndBooking() {

        List<WebElement> dateInputs =
                getDateInputs();

        WebElement checkIn =
                dateInputs.get(0);

        setDate(
                checkIn,
                "20/11/2026"
        );

        dateInputs =
                getDateInputs();

        WebElement checkOut =
                dateInputs.get(1);

        setDate(
                checkOut,
                "22/11/2026"
        );

        WebElement checkAvailability =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath(
                                        "//button[contains(.,'Check Availability')]"
                                )
                        )
                );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                checkAvailability
        );

        checkAvailability.click();

        WebElement singleCard =
                wait.until(driver -> {

                    List<WebElement> cards =
                            driver.findElements(
                                    By.cssSelector(
                                            ".room-card"
                                    )
                            );

                    return cards.stream()
                            .filter(WebElement::isDisplayed)
                            .filter(card ->
                                    card.findElements(
                                            By.xpath(
                                                    ".//*[normalize-space()='Single']"
                                            )
                                    ).size() > 0
                            )
                            .findFirst()
                            .orElse(null);
                });

        assertNotNull(
                singleCard,
                "Картка Single має відображатися"
        );

        String cardText =
                singleCard.getText();

        assertAll(
                "Дані картки Single",
                () -> assertTrue(
                        cardText.contains("Single")
                ),
                () -> assertTrue(
                        cardText.contains("£100")
                ),
                () -> assertTrue(
                        cardText.contains("per night")
                ),
                () -> assertTrue(
                        cardText.contains("TV")
                ),
                () -> assertTrue(
                        cardText.contains("WiFi")
                ),
                () -> assertTrue(
                        cardText.contains("Safe")
                )
        );

        WebElement bookNow =
                singleCard.findElements(
                                By.cssSelector(
                                        "a, button"
                                )
                        )
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .filter(element ->
                                element.getText()
                                        .trim()
                                        .equalsIgnoreCase(
                                                "Book now"
                                        )
                        )
                        .findFirst()
                        .orElseThrow();

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                bookNow
        );

        bookNow.click();

        wait.until(
                driver ->
                        driver.getCurrentUrl()
                                .contains(
                                        "/reservation/"
                                )
        );

        String url =
                driver.getCurrentUrl();

        assertAll(
                "Перехід до бронювання",
                () -> assertTrue(
                        url.contains(
                                "/reservation/"
                        ),
                        "URL має містити /reservation/"
                ),
                () -> assertTrue(
                        url.contains(
                                "checkin=2026-11-20"
                        ),
                        "URL має містити дату заїзду"
                ),
                () -> assertTrue(
                        url.contains(
                                "checkout=2026-11-22"
                        ),
                        "URL має містити дату виїзду"
                )
        );
    }
}
