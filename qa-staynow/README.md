# StayNow Project

Модуль автоматизованого тестування системи «СтейНау» (v1).

## Автоматизоване тестування (Sprint 4)

Для запуску повного набору юніт- та UI-тестів проєкту використовується Apache Maven та JUnit 5 (із підтримкою Selenium 4 для UI-сценаріїв).

### Вимоги до середовища:
* Java SDK: 17 або вище
* Build Tool: Apache Maven 3.8+
* Браузер: Google Chrome (Selenium 4 автоматично керує ChromeDriver)

### Команди для запуску тестів:

1. Повний запуск усіх тестів (включно з UI у Headless-режимі):
   mvn clean test

2. Примусовий запуск усіх тегованих тест-груп:
   mvn test -DexcludedGroups=none

### Структура автоматизованих тестів:
* Юніт-тести (JUnit 5): src/test/java/ua/kpi/tzyapz/staynow/*Test.java (UT-01 – UT-20)
* UI-тести (Selenium 4): src/test/java/ua/kpi/tzyapz/staynow/*UiTest.java (UI-01 – UI-08)

