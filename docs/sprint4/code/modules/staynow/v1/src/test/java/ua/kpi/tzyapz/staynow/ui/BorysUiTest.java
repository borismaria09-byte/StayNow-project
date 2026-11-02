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

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
@DisplayName("Sprint 4: UI-тести UI-05–UI-06 (Виконавець: Борис Марія)")
class BorysUiTest {

    private static final String BASE_URL =
            "https://automationintesting.online/";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--window-size=1280,900"
        );

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
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
                () -> assertTrue(header.getText().contains("Shady Meadows B&B")),
                () -> assertTrue(header.getText().contains("Rooms")),
                () -> assertTrue(header.getText().contains("Booking")),
                () -> assertTrue(header.getText().contains("Amenities")),
                () -> assertTrue(header.getText().contains("Location")),
                () -> assertTrue(header.getText().contains("Contact")),
                () -> assertTrue(header.getText().contains("Admin"))
        );

        WebElement contact = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Send Us a Message')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                contact
        );

        Boolean headerVisible =
                (Boolean) ((JavascriptExecutor) driver).executeScript(
                        "const r = arguments[0].getBoundingClientRect();" +
                        "return r.bottom > 0 && r.top < window.innerHeight;",
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

        WebElement checkIn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[placeholder*='Check In']")
                )
        );

        WebElement checkOut = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[placeholder*='Check Out']")
                )
        );

        checkIn.clear();
        checkIn.sendKeys("20/11/2026");
        checkOut.clear();
        checkOut.sendKeys("22/11/2026");

        WebElement checkAvailability = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(.,'Check Availability')]")
                )
        );

        checkAvailability.click();
        WebElement singleCard = wait.until(driver -> {

            List<WebElement> cards =
                    driver.findElements(By.cssSelector("div"));

            return cards.stream()
                    .filter(WebElement::isDisplayed)
                    .filter(card -> {
                        String text = card.getText();

                        return text.contains("Single")
                                && text.contains("£100 per night")
                                && text.contains("TV")
                                && text.contains("WiFi")
                                && text.contains("Safe")
                                && text.toLowerCase().contains("book now");
                    })
                    .findFirst()
                    .orElse(null);
        });

        assertNotNull(
                singleCard,
                "Картка Single має відображатися"
        );

        String cardText = singleCard.getText();

        assertAll(
                "Дані картки Single",
                () -> assertTrue(cardText.contains("Single")),
                () -> assertTrue(cardText.contains("£100 per night")),
                () -> assertTrue(cardText.contains("TV")),
                () -> assertTrue(cardText.contains("WiFi")),
                () -> assertTrue(cardText.contains("Safe"))
        );

        WebElement bookNow = singleCard.findElements(
                        By.cssSelector("a, button")
                )
                .stream()
                .filter(WebElement::isDisplayed)
                .filter(element ->
                        element.getText()
                                .trim()
                                .equalsIgnoreCase("Book now")
                )
                .findFirst()
                .orElseThrow();

        wait.until(
                ExpectedConditions.elementToBeClickable(bookNow)
        ).click();

        wait.until(
                driver -> driver.getCurrentUrl()
                        .contains("/reservation/")
        );

        String url = driver.getCurrentUrl();

        assertAll(
                "Перехід до бронювання",
                () -> assertTrue(
                        url.contains("/reservation/"),
                        "URL має містити /reservation/"
                ),
                () -> assertTrue(
                        url.contains("checkin=2026-11-20"),
                        "URL має містити дату заїзду"
                ),
                () -> assertTrue(
                        url.contains("checkout=2026-11-22"),
                        "URL має містити дату виїзду"
                )
        );
    }
}
