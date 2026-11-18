# StayNow Project

Модуль автоматизованого тестування системи «СтейНау» (v1).

## Автоматизоване тестування (Sprint 4)

Для запуску повного набору юніт- та UI-тестів проєкту використовується Apache Maven та JUnit 5 (із підтримкою Selenium 4 для UI-сценаріїв).

### Вимоги до середовища
- **Java SDK:** 17 або вище
- **Build Tool:** Apache Maven 3.8+
- **Браузер:** Google Chrome (Selenium 4 автоматично керує ChromeDriver у headless-режимі)

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
- **Юніт-тести (JUnit 5):** `src/test/java/ua/kpi/tzyapz/staynow/*Test.java` (`UT-01` – `UT-20`)
  - `BorysTest.java` — Борис Марія
  - `MironenkoTest.java` — Мироненко
  - `MykhailenkoTest.java` — Михайленко
  - `ProtsenkoTest.java` — Проценко Давид-Сергій (`UT-01` – `UT-04`)
  - `VoloshynTest.java` — Волошин
- **UI-тести (Selenium 4):** `src/test/java/ua/kpi/tzyapz/staynow/ui/*UiTest.java` (`UI-01` – `UI-08`)
  - `ProtsenkoUiTest.java` — Проценко Давид-Сергій (`UI-01` – `UI-02`)

---

## Підсумки автоматизації (Sprint 4 — Досьє якості v4)

- **Репозиторій автотестів:** https://github.com/borismaria09-byte/StayNow-project
- **Трекер дефектів модуля (`module`):** https://github.com/borismaria09-byte/StayNow-project/issues?q=is%3Aissue+label%3Amodule

### 1. Таблиця нормативу (План / Факт)

| Показник | План | Факт | Статус |
|---|---|---|---|
| Загальна кількість автотестів (Unit + UI) | 54 | 54 | Виконано (100%) |
| Успішно пройдено (`PASSED`) | 54 | 41 | Pass Rate: 75.9% |
| Впало через дефекти модуля (`FAILED`) | 0 | 13 | Заведено 7 дефектів (`#12`, `#13`, `#19`–`#23`) |
| Помилки середовища / збірки (`ERRORS`) | 0 | 0 | Стабільне середовище запуску |

### 2. Артефакти покриття та звітності

- **Матриця покриття BL:** [`docs/sprint4/coverage-matrix.md`](docs/sprint4/coverage-matrix.md)
- **Перелік UI-сценаріїв:** [`docs/sprint4/ui-scenarios.md`](docs/sprint4/ui-scenarios.md)
- **Підсумок Maven Surefire:** [`docs/sprint4/surefire-summary.md`](docs/sprint4/surefire-summary.md)
- **Звіт аудиту спринту:** [`docs/sprint4/audit-report.md`](docs/sprint4/audit-report.md)

### 3. Виявлені дефекти модуля (мітка `module`)

| ID дефекту | Вимога | Тести, що впали (`FAILED`) | Короткий опис проблеми | Severity |
|---|---|---|---|---|
| **#12** | BL-10 | `MykhailenkoTest.checkInBeforeTodayIsRejected` | Система приймає дату заїзду в минулому | Major |
| **#13** | BL-50 | `MykhailenkoTest.nightPriceIsRoundedHalfUp` | Некоректне округлення вартості бронювання (`43.32` замість `43.33`) | Minor |
| **#19** | BL-10 | `BorysTest.thirtyNightsAreAllowed` | Система відхиляє валідне бронювання тривалістю рівно 30 ночей | Major |
| **#20** | BL-70 | `BorysTest.checkInOnPreviousCheckOutDateIsAvailable`, `VoloshynTest.ut12_bookingDateOverlap` | Помилка перевірки перетину дат та заїзду в день виїзду попереднього гостя | Major |
| **#21** | BL-40 | `BorysTest.fridayNightHasWeekendSurcharge`, `MironenkoTest.isWeekendNight_Test` | Невірне визначення вихідних ночей та відсутність націнки вихідного дня | Major |
| **#22** | BL-50 | `VoloshynTest.ut10_longStayDiscount` | Некоректний розрахунок знижки 10% за тривале проживання (`615.00` замість `630.00`) | Major |
| **#23** | BL-60 | `BorysTest.cancellationExactlySevenDaysBeforeIsFree`, `MironenkoTest.cancellationFee_7Days_IsFree`, `VoloshynTest.ut11_cancellationFee` | Нараховується штраф `500.00` замість `0.00` при скасуванні рівно за 7 днів | Major |

### 4. Висновок про стан модуля v1

Тестовий набір із 54 модульних та UI-тестів повністю інтегрований у репозиторій та стабільно виконується через Maven Surefire і Selenium WebDriver 4 у headless-режимі (`Errors: 0`). Під час прогону виявлено 13 падінь, пов'язаних із 7 дефектами бізнес-логіки модуля v1 (`#12`, `#13`, `#19`–`#23`). Модуль v1 **не готовий до релізу** і потребує виправлення заведених дефектів із міткою `module`.
