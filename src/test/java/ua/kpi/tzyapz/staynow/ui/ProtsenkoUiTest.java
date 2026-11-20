package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
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

@Tag("ui")
@DisplayName("Sprint 4: UI-тести UI-01–UI-02 (Виконавець: Проценко Д.-С.)")
class ProtsenkoUiTest {

    private static final String BASE_URL =
            "https://automationintesting.online/";

    private static final String ADMIN_URL =
            BASE_URL + "admin";

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

        driver.get(ADMIN_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("UI-01 / TC-025 / GUI-70: на сторінці входу відображаються Username, Password і Login")
    void testUi01_adminLoginFormElementsAreVisible() {

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#username, input[placeholder='Enter username']"
                        )
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#password, input[placeholder='Password']"
                        )
                )
        );

        WebElement loginButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#doLogin, button[type='submit']"
                        )
                )
        );

        assertAll(
                "TC-025 / GUI-70: елементи форми Login",
                () -> assertTrue(
                        username.isDisplayed(),
                        "Поле Username має бути видимим"
                ),
                () -> assertTrue(
                        password.isDisplayed(),
                        "Поле Password має бути видимим"
                ),
                () -> assertTrue(
                        loginButton.isDisplayed(),
                        "Кнопка Login має бути видимою"
                )
        );
    }

    @Test
    @DisplayName("UI-02 / TC-021 / F-80: неправильний пароль не дає доступу до адмінпанелі")
    void testUi02_invalidPasswordDoesNotLogin() {

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#username, input[placeholder='Enter username']"
                        )
                )
        );

        WebElement password = driver.findElement(
                By.cssSelector(
                        "#password, input[placeholder='Password']"
                )
        );

        username.sendKeys("admin");
        password.sendKeys("wrong-password");

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#doLogin, button[type='submit']"
                        )
                )
        );

        loginButton.click();

        WebElement usernameAfter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#username, input[placeholder='Enter username']"
                        )
                )
        );

        assertAll(
                "TC-021 / F-80: неправильний пароль",
                () -> assertFalse(
                        driver.getCurrentUrl().contains("/admin/rooms"),
                        "При неправильному паролі не має бути переходу в /admin/rooms"
                ),
                () -> assertTrue(
                        usernameAfter.isDisplayed(),
                        "Форма входу має залишатися доступною"
                )
        );
    }
}
