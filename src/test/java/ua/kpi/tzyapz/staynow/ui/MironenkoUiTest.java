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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("Sprint 4: UI-тести (Виконавець: Міроненко Микита)")
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
    @DisplayName("UI-03 | Перевірка завантаження головної сторінки та заголовку готелю")
    void homePageLoadsSuccessfully() {
        driver.get(BASE_URL);
        
        // Чекаємо на появу назви готелю у шапці за допомогою явного очікування
        String brandName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".hotel-name"))
        ).getText();

        assertTrue(brandName.contains("Shady Meadows") || !brandName.isEmpty(), 
                "Назва готелю має відображатися на сторінці");
    }

    @Test
    @DisplayName("UI-04 | Перевірка відкриття сторінки входу в адмінпанель")
    void adminLinkOpensLogin() {
        driver.get(BASE_URL);

        // Клікаємо на посилання Admin у меню
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Admin"))).click();

        // Перевіряємо, що перейшли на сторінку /admin
        wait.until(ExpectedConditions.urlContains("/admin"));
        
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/admin"), "URL повинен містити /admin");
    }
}