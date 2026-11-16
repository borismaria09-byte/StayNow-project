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
    @DisplayName("UI-03 / TC-001: Перевірка завантаження головної сторінки та заголовку готелю")
    void homePageLoadsSuccessfully() {
        driver.get(BASE_URL);
        
        String brandName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".hotel-name"))
        ).getText();

        assertTrue(brandName.contains("Shady Meadows B&B"), 
                "Назва готелю 'Shady Meadows B&B' має відображатися у шапці сторінки");
    }
}
