# StayNow Project

Модуль автоматизованого тестування системи «СтейНау» (v1).

## Автоматизоване тестування (Sprint 4)

Для запуску повного набору юніт- та UI-тестів проєкту використовується Apache Maven та JUnit 5 (із підтримкою Selenium 4 для UI-сценаріїв).

### Вимоги до середовища

* **Java SDK:** 17 або вище
* **Build Tool:** Apache Maven 3.8+
* **Браузер:** Google Chrome (Selenium 4 автоматично керує ChromeDriver у headless-режимі)

### Команди для запуску тестів

1. Запуск тестів без групи `ui` (тільки модульні тести):

```bash
mvn clean test
```

2. Запуск повного набору тестів, включно з UI (у Headless-режимі):

```bash
mvn test -DexcludedGroups=none
```

### Структура автоматизованих тестів та автори

* **Юніт-тести (JUnit 5):** `src/test/java/ua/kpi/tzyapz/staynow/*Test.java` (`UT-01` – `UT-20`)
  * `BorysTest.java` — Борис Марія
  * `MironenkoTest.java` — Міроненко Микита
  * `MykhailenkoTest.java` — Михайленко Ростислав
  * `ProtsenkoTest.java` — Проценко Давид-Сергій (`UT-01` – `UT-04`)
  * `VoloshynTest.java` — Волошин Дмитро

* **UI-тести (Selenium 4):** `src/test/java/ua/kpi/tzyapz/staynow/ui/*UiTest.java` (`UI-01` – `UI-08`)
  * `ProtsenkoUiTest.java` — Проценко Давид-Сергій (`UI-01`–`UI-02`)
  * `MironenkoUiTest.java` — Міроненко Микита (`UI-03`)
  * `VoloshynUiTest.java` — Волошин Дмитро (`UI-04`)
  * `BorysUiTest.java` — Борис Марія (`UI-05`–`UI-06`)
  * `MykhailenkoUiTest.java` — Михайленко Ростислав (`UI-07`–`UI-08`)

---

## Підсумки автоматизації (Sprint 4 — Досьє якості v4)

* **Репозиторій автотестів:** https://github.com/borismaria09-byte/StayNow-project
* **Трекер дефектів модуля (`module`):** https://github.com/borismaria09-byte/StayNow-project/issues?q=is%3Aissue+label%3Amodule

### 1. Таблиця нормативу (План / Факт)

| Показник | План | Факт | Статус | 
| ----- | ----- | ----- | ----- | 
| Автоматизовані тест-методи за нормативом | 28 | 28 (20 JUnit + 8 UI) | Виконано (100%) |
| Виконань тестів у Maven Surefire | — | 54 | Зафіксовано у Surefire |
| Успішно пройдено (`PASSED`) | 54 | 41 | Pass Rate: 75.9% | 
| Впало через дефекти модуля (`FAILED`) | 0 | 13 | Заведено 6 дефектів (`#12`, `#13`, `#19`, `#25`, `#26`, `#28`) | 
| Помилки середовища / збірки (`ERRORS`) | 0 | 0 | Стабільне середовище запуску | 

### 2. Артефакти покриття та звітності

* **Матриця покриття BL:** [`docs/sprint4/coverage-matrix.md`](docs/sprint4/coverage-matrix.md)
* **Перелік UI-сценаріїв:** [`docs/sprint4/ui-scenarios.md`](docs/sprint4/ui-scenarios.md)
* **Підсумок Maven Surefire:** [`docs/sprint4/surefire-summary.md`](docs/sprint4/surefire-summary.md)
* **Звіт аудиту спринту:** [`docs/sprint4/audit-report.md`](docs/sprint4/audit-report.md)

### 3. Виявлені дефекти модуля (мітка `module`)

| ID дефекту | Вимога | Тести, що впали (`FAILED`) | Короткий опис проблеми | Severity | 
| ----- | ----- | ----- | ----- | ----- | 
| **#12** | BL-10 | `MykhailenkoTest.checkInBeforeTodayIsRejected` | Система приймає дату заїзду в минулому | Major | 
| **#13** | BL-50 | `MykhailenkoTest.nightPriceIsRoundedHalfUp` | Некоректне округлення вартості бронювання (`43.32` замість `43.33`) | Major | 
| **#19** | BL-10 | `BorysTest.thirtyNightsAreAllowed` | Система відхиляє валідне бронювання тривалістю рівно 30 ночей | Major | 
| **#25** | BL-60 | `VoloshynTest.ut11_cancellationFee` | При скасуванні рівно за 7 днів нараховується штраф `500.00` замість `0.00` | Major |
| **#26** | BL-70 | `VoloshynTest.ut12_bookingDateOverlap` | Стикове бронювання помилково визначається як перетин дат | Major |
| **#28** | BL-30 | `VoloshynTest.ut10_longStayDiscount` | Не застосовується надбавка +15% для п'ятниці | Major |

### 4. Висновок про стан модуля v1

Тестовий набір із 28 автоматизованих тест-методів (20 JUnit та 8 UI) повністю інтегрований у репозиторій. Під час повного запуску Maven Surefire виконано 54 тестові запуски з урахуванням параметризованих сценаріїв; зафіксовано 13 падінь, пов'язаних із 6 дефектами бізнес-логіки модуля v1 (`#12`, `#13`, `#19`, `#25`, `#26`, `#28`). Помилок виконання (`Errors`) не зафіксовано. Модуль v1 **не готовий до релізу** і потребує виправлення заведених дефектів із міткою `module`.
