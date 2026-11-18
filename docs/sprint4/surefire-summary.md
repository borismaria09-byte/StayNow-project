# Підсумок виконання автоматизованого тестування Maven Surefire (Sprint 4)

**Продукт:** «СтейНау» (staynow-module v1)  
**Спринт:** 4  
**Дата прогону:** 16.11.2026  
**Виконавець / Відповідальний КТ:** Проценко Давид-Сергій  
**Рецензент:** Борис Марія  

---

## 1. Загальні параметри запуску

- **Середовище:** Windows 10/11, OpenJDK 17.0.12, Apache Maven 3.9.16
- **Конфігурація браузера для UI-тестів:** Google Chrome (Headless mode `--headless=new`), Selenium WebDriver 4.x
- **Команди виконання:**
  1. `mvn clean test` (запуск без групи `ui`)
  2. `mvn test -DexcludedGroups=none` (повний запуск із групою `ui`)

---

## 2. Підсумкові показники збірки (Build Summary)

### Запуск 1: `mvn clean test` (тільки юніт-тести)
**Вивід консолі:**
```text
Tests run: 46, Failures: 13, Errors: 0, Skipped: 0
```
| Метрика | Значення |
|---|---|
| **Усього виконано тестів** | **46** |
| **Падіння (Failures)** | **13** |
| **Помилки (Errors)** | **0** |
| **Статус збірки** | **BUILD FAILURE** |

### Запуск 2: `mvn test -DexcludedGroups=none` (Unit + UI тести)
**Вивід консолі:**
```text
Tests run: 54, Failures: 13, Errors: 0, Skipped: 0
```
| Метрика | Значення |
|---|---|
| **Усього виконано тестів** | **54** |
| **Падіння (Failures)** | **13** |
| **Помилки (Errors)** | **0** |
| **Статус збірки** | **BUILD FAILURE** |

---

## 3. Деталізація за тестовими класами

### 3.1. `ua.kpi.tzyapz.staynow.ProtsenkoTest` та `ProtsenkoUiTest`
- **Failures:** 0, **Errors:** 0
- **Результат:** `PASSED` (Усі тести виконані успішно без відхилень).

### 3.2. Інші класи (`BorysTest`, `MironenkoTest`, `MykhailenkoTest`, `VoloshynTest`)
- **Failures:** 13 (сумарно)
- **Результат:** `FAILED` (Присутні помилки валідації дат, розрахунку націнки вихідного дня, округлення, знижок та скасування бронювання; зафіксовано 7 дефектів із міткою `module`).

---

## 4. Аналіз «червоних» тестів та дефектів (мітка `module`)

| ID дефекту | Вимога | Тести, що впали (`FAILED`) | Опис проблеми | Severity |
|---|---|---|---|---|
| **#12** | BL-10 | `MykhailenkoTest.checkInBeforeTodayIsRejected` | Система приймає дату заїзду в минулому | Major |
| **#13** | BL-50 | `MykhailenkoTest.nightPriceIsRoundedHalfUp` | Некоректне округлення вартості бронювання (`43.32` замість `43.33`) | Minor |
| **#19** | BL-10 | `BorysTest.thirtyNightsAreAllowed` | Система відхиляє валідне бронювання тривалістю рівно 30 ночей | Major |
| **#20** | BL-70 | `BorysTest.checkInOnPreviousCheckOutDateIsAvailable`, `VoloshynTest.ut12_bookingDateOverlap` | Помилка перевірки перетину дат та заїзду в день виїзду попереднього гостя | Major |
| **#21** | BL-40 | `BorysTest.fridayNightHasWeekendSurcharge`, `MironenkoTest.isWeekendNight_Test` | Невірне визначення вихідних ночей та відсутність націнки вихідного дня | Major |
| **#22** | BL-50 | `VoloshynTest.ut10_longStayDiscount` | Некоректний розрахунок знижки 10% за тривале проживання (`615.00` замість `630.00`) | Major |
| **#23** | BL-60 | `BorysTest.cancellationExactlySevenDaysBeforeIsFree`, `MironenkoTest.cancellationFee_7Days_IsFree`, `VoloshynTest.ut11_cancellationFee` | Нараховується штраф `500.00` замість `0.00` при скасуванні рівно за 7 днів | Major |

---

## 5. Висновок керівника тестування (КТ)

1. Команда успішно інтегрувала всі тести в єдиний репозиторій. Загальна кількість виконаних тестів: 54.
2. Локальні тести розробника `ProtsenkoTest` та `ProtsenkoUiTest` проходять успішно (`Errors: 0`).
3. Виявлено 13 падінь (`Failures`) у тестах інших учасників через розбіжності між очікуваною бізнес-логікою та фактичною реалізацією модуля v1.
4. Усі 7 виявлених дефектів занесені в трекер GitHub Issues (`#12`, `#13`, `#19`–`#23`) з міткою `module` для подальшого виправлення.
5. Протокол запуску та прив'язку до дефектів задокументовано у повному обсязі.
