
# UI-сценарії (WebDriver)

| ID / TC Sprint 3 | Передумови та тестові дані | Кроки та локатори | Очікування | Тест-метод |
| :--- | :--- | :--- | :--- | :--- |
| **UI-01** (TC-001) | Без авторизації. Нова сесія. | 1. get("/")<br>2. Очікування `visibilityOfElementLocated(By.cssSelector(".hotel-description"))` | Відображається заголовок "Welcome to Shady Meadows B&B" та кнопка "Book Now". | `homePageLoadsSuccessfully()` |
| **UI-02** (TC-002) | Дати: 20/11/2026 – 22/11/2026. | 1. Клік `By.name("checkin")` -> ввід дат.<br>2. Клік `By.cssSelector("button.check-availability")` | Відкривається картка Single з ціною £100 per night та зручностями. | `checkAvailabilityShowsSingleRoom()` |
| **UI-03** (TC-003) | Номер доступний. Дані: T3-Test, T3-Testenko, t3@example.com, +380001234567 | 1. Клік `By.xpath("//button[text()='Book this room']")`<br>2. Ввід даних у `By.name("firstname")`, `lastname`, `email`, `phone`<br>3. Клік `By.cssSelector(".btn-book")` | З'являється повідомлення "Booking Confirmed". | `successfulBookingShowsConfirmation()` |
| **UI-04** (TC-005) | Головна сторінка відкрита. | 1. Прокрутка до `By.cssSelector("#contact")`<br>2. Перевірка `isDisplayed()` для `By.cssSelector("nav.navbar")` | Шапка сторінки (Rooms, Booking, Contact) залишається видимою. | `navbarRemainsVisibleOnScroll()` |
| **UI-05** (TC-008) | Відкрито форму номера Single. | 1. get("/reservation/1")<br>2. Очікування `visibilityOfElementLocated(By.cssSelector(".price-summary"))` | Відображається блок Price Summary з рядками "Service fee £15" та "Total". | `priceSummaryDisplaysCorrectFees()` |
| **UI-06** (TC-009) | Форма відкрита, поля порожні. | 1. Клік `By.cssSelector(".btn-book")` з порожніми полями.<br>2. Читання `By.cssSelector(".alert-danger")` | Бронювання не створюється, з'являються помилки валідації полів. | `emptyFormSubmitShowsValidationErrors()` |
| **UI-07** (TC-010) | Відкрито форму номера Single. | 1. Клік `By.name("checkin")`<br>2. Очікування `visibilityOfElementLocated(By.cssSelector(".react-datepicker"))` | Відкривається віджет календаря вибору дат. | `calendarWidgetOpensOnClick()` |
| **UI-08** (F-80) | Облікові дані: admin / password | 1. get("/admin")<br>2. Ввід у `By.id("username")` та `By.id("password")`<br>3. Клік `By.id("login")` | URL змінюється на панель керування, з'являються повідомлення гостей. | `adminLoginSuccess()` |
