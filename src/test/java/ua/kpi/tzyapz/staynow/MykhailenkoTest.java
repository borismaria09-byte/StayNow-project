
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("Sprint 4: UI-тести UI-07–UI-08 (Виконавець: Михайленко Р.С.)")
class MykhailenkoUiTest {

    private static final String BASE_URL = "https://automationintesting.online/";
    private static final String ADMIN_URL = BASE_URL + "admin";

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
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement visible(By by) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(by)
        );
    }

    private void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );
    }

    @Test
    @DisplayName("UI-07 / TC-020 / F-80: адміністратор з правильними даними потрапляє в розділ номерів, є Logout")
    void adminLoginOpensRoomsSection() {
        driver.get(ADMIN_URL);

        // Передумова: сторінка входу відкрилася
        visible(By.cssSelector(
                "#username, input[placeholder='Enter username']"
        )).sendKeys("admin");

        driver.findElement(By.cssSelector(
                "#password, input[placeholder='Password']"
        )).sendKeys("password");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#doLogin, button[type='submit']")
        )).click();

        // Очікуємо переходу до розділу керування номерами
        wait.until(ExpectedConditions.urlContains("/admin/rooms"));

        WebElement logout = visible(By.xpath(
                "//*[self::a or self::button][normalize-space()='Logout']"
        ));

        assertAll("Успішний вхід до адмінпанелі",
                () -> assertTrue(
                        driver.getCurrentUrl().contains("/admin/rooms"),
                        "URL має вказувати на розділ номерів: "
                                + driver.getCurrentUrl()
                ),
                () -> assertTrue(
                        logout.isDisplayed(),
                        "Кнопка Logout має бути видимою"
                ),
                () -> assertFalse(
                        driver.findElements(By.cssSelector(
                                "#doLogin, input[placeholder='Enter username']"
                        )).stream().anyMatch(WebElement::isDisplayed),
                        "Форма входу не має лишатися на екрані"
                )
        );
    }

    @Test
    @DisplayName("UI-08 / TC-014 / F-70: валідна контактна форма замінюється подякою з іменем і темою")
    void contactFormShowsThankYouWithNameAndSubject() {
        driver.get(BASE_URL);

        // Заповнюємо контактну форму
        WebElement name = visible(By.id("name"));
        scrollTo(name);

        name.sendKeys("John");

        driver.findElement(By.id("email"))
                .sendKeys("test@test.com");

        driver.findElement(By.id("phone"))
                .sendKeys("+380123456789");

        driver.findElement(By.id("subject"))
                .sendKeys("Question about booking");

        driver.findElement(By.id("description"))
                .sendKeys("I would like to ask about room availability.");

        // Знаходимо кнопку Submit
        By submitLocator = By.cssSelector(
                "#contact form button.btn-primary"
        );

        WebElement submitButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(submitLocator)
        );

        // Прокручуємо сторінку до кнопки
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});",
                submitButton
        );

        // Перевіряємо, що кнопка знаходиться в області екрана
        // і її не перекриває інший елемент
        wait.until(d -> {
            Boolean ready = (Boolean) ((JavascriptExecutor) d).executeScript("""
                    const button = arguments[0];
                    const r = button.getBoundingClientRect();
                    const x = r.left + r.width / 2;
                    const y = r.top + r.height / 2;

                    return x >= 0 && x < window.innerWidth
                        && y >= 0 && y < window.innerHeight
                        && document.elementFromPoint(x, y) === button;
                    """, submitButton);

            return Boolean.TRUE.equals(ready);
        });

        // Натискаємо Submit
        submitButton.click();

        // Очікуємо появи тексту подяки
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("body"),
                "Question about booking"
        ));

        String page = driver.findElement(
                By.cssSelector("body")
        ).getText();

        // Перевіряємо результат відправлення форми
        assertAll("Подяка замість форми",
                () -> assertTrue(
                        page.contains("John"),
                        "Подяка має містити ім'я гостя"
                ),
                () -> assertTrue(
                        page.contains("Question about booking"),
                        "Подяка має містити тему"
                ),
                () -> assertTrue(
                        driver.findElements(By.cssSelector(
                                "#contact form button.btn-primary"
                        )).stream().noneMatch(WebElement::isDisplayed),
                        "Кнопка Submit має зникнути після відправлення"
                )
        );
    }
}
