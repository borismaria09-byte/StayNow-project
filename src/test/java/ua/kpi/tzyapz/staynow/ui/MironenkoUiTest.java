package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("Sprint 4: UI-тест UI-03 (Виконавець: Міроненко Микита)")
class MironenkoUiTest {

    private static final String BASE_URL = "https://automationintesting.online/";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("UI-03 / TC-003: Створення бронювання номера з коректними даними")
    void successfulBookingShowsConfirmation() {
        driver.get(BASE_URL + "reservation/1?checkin=2027-04-05&checkout=2027-04-07");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("doReservation"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstname"))).sendKeys("T3-Test");
        driver.findElement(By.name("lastname")).sendKeys("T3-Testenko");
        driver.findElement(By.name("email")).sendKeys("t3@example.com");
        driver.findElement(By.name("phone")).sendKeys("+380001234567");

        driver.findElement(By.cssSelector("button.btn-primary")).click();

        String confirmationText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".confirmation-modal, .card-body"))
        ).getText();

        assertTrue(confirmationText.contains("Booking Confirmed"),
                "Має з'явитися повідомлення 'Booking Confirmed'");
    }
}
