package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
@DisplayName("Sprint 4: UI-тести UI-05–UI-06 (Виконавець: Борис Марія)")
class BorysUiTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://automationintesting.online/");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("UI-05 / TC-005 / GUI-10: шапка залишається видимою під час прокручування")
    void testUi05_headerRemainsVisible() {

        WebElement header = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("nav"))
        );
      
        WebElement contact = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Send Us a Message')]")
                )
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView();", contact);

        assertAll(
                () -> assertTrue(header.isDisplayed()),
                () -> assertTrue(header.getText().contains("Rooms")),
                () -> assertTrue(header.getText().contains("Booking")),
                () -> assertTrue(header.getText().contains("Amenities")),
                () -> assertTrue(header.getText().contains("Location")),
                () -> assertTrue(header.getText().contains("Contact")),
                () -> assertTrue(header.getText().contains("Admin"))
        );
    }

    @Test
    @DisplayName("UI-06 / TC-006 / BL-70: Single недоступний для інтервалу, що перетинається з бронюванням")
    void testUi06_singleUnavailableForOverlappingDates() {

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

        checkIn.sendKeys("11/03/2027");
        checkOut.sendKeys("13/03/2027");

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(.,'Check Availability')]")
                )
        );

        button.click();

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))
        );

        String result = body.getText();

        assertAll(
                () -> assertFalse(
                        result.contains("Single\n"),
                        "Single не повинен бути доступним для перекривного інтервалу"
                ),
                () -> assertTrue(
                        result.contains("Double") || result.contains("Suite"),
                        "Інші доступні номери мають відображатися"
                )
        );
    }
}
