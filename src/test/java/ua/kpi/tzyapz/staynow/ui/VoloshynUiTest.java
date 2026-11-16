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
@DisplayName("StayNow UI: сценарій UI-04 (валідація форми бронювання)")
class VoloshynUiTest {

    private static final String BASE_URL = "https://automationintesting.online/";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(BASE_URL);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("UI-04 [TC-013]: валідація форми бронювання при невалідному Email та короткому Phone")
    void ui04_bookingFormRejectsInvalidEmailAndShortPhone() {
        By bookBtnLocator = By.cssSelector("a.btn-primary[href*='reservation'], button.openBooking");
        WebElement bookBtn = wait.until(ExpectedConditions.elementToBeClickable(bookBtnLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", bookBtn);
        bookBtn.click();

        List<WebElement> reserveNowButtons = driver.findElements(By.id("doReservation"));
        if (!reserveNowButtons.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("doReservation"))).click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstname")));

        driver.findElement(By.name("firstname")).sendKeys("Dmytro");
        driver.findElement(By.name("lastname")).sendKeys("Voloshyn");
        driver.findElement(By.name("email")).sendKeys("invalid-email");
        driver.findElement(By.name("phone")).sendKeys("12345");

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Book') or contains(text(),'Reserve')]")));
        submitBtn.click();

        WebElement alertBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert.alert-danger")));
        String errorText = alertBox.getText();

        assertAll("UI-04: перевірка повідомлень валідації",
                () -> assertTrue(alertBox.isDisplayed(), "Блок помилок має відображатися"),
                () -> assertTrue(errorText.toLowerCase().contains("email"),
                        "Очікували помилку email, отримано: " + errorText),
                () -> assertTrue(errorText.contains("11") && errorText.contains("21"),
                        "Очікували помилку довжини телефону (11..21), отримано: " + errorText)
        );
    }
}