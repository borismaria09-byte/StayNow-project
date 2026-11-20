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
@DisplayName("StayNow UI: сценарій UI-04")
class VoloshynUiTest {

    private static final String BASE_URL =
            "https://automationintesting.online/";

    private static final String ADMIN_URL =
            BASE_URL + "admin";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
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
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("UI-04 / TC-023 / F-80: порожні поля не дозволяють увійти до адмінпанелі")
    void ui04_emptyLoginFieldsDoNotLogin() {

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
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#doLogin, button[type='submit']"
                        )
                )
        );

        loginButton.click();

        WebElement loginButtonAfter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#doLogin, button[type='submit']"
                        )
                )
        );

        assertAll(
                "TC-023 / F-80: порожні поля Login",
                () -> assertTrue(
                        username.getAttribute("value").isEmpty(),
                        "Username має залишатися порожнім"
                ),
                () -> assertTrue(
                        password.getAttribute("value").isEmpty(),
                        "Password має залишатися порожнім"
                ),
                () -> assertFalse(
                        driver.getCurrentUrl().contains("/admin/rooms"),
                        "Не має бути переходу до адмінпанелі"
                ),
                () -> assertTrue(
                        loginButtonAfter.isDisplayed(),
                        "Кнопка Login має залишатися на сторінці"
                )
        );
    }
}
