# DailyBloom - приложение для отслеживания привычек

Ежедневное приложение для учета и формирования привычек: добавляйте новые привычки с настраиваемой частотой, отмечайте их выполнение, фильтруйте и сортируйте.

![Главный экран](app/src/main/res/assets/images/main_screen.png)
![Главный экран](app/src/main/res/assets/images/main_screen_bad.png)
![Экран меню](app/src/main/res/assets/images/habits_menu.png)
![Экран редактирования](app/src/main/res/assets/images/create_habit.png)

---
## 📖 Оглавление

- [Особенности](#-особенности)
- [Архитектура](#-архитектура)
- [Модули проекта](#-модули-проекта)
- [Технологии и библиотеки](#-технологии-и-библиотеки)
- [ViewModel](#-viewmodel)

---

## Особенности

- Добавление привычек с выбором частоты напоминаний
- Разделение привычек на «полезные» и «вредные» во ViewPager
- NavController управляет переходами между Fragment’ами
- Отметка выполнения привычки в списке
- Bottom Sheet для поиска по названию и сортировке привычек
- Автоматический sync с бэкендом и локальным хранилищем Room
- Поддержка Clean Architecture + Dagger + Coroutines & Flow

---

## Архитектура

Приложение реализовано по принципам Clean Architecture с разделением на 3 слоя:

1. **data**  
   – Реализация репозиториев, источников данных (локальный Room + Retrofit).
2. **domain**  
   – Интерфейсы репозиториев, бизнес-логика (use-cases), модель Habit.
3. **presentation**  
   – ViewModel, Fragment/Activity, UI-компоненты.

Связь между слоями организована через **Hilt**

---

## ⚙️ Технологии и библиотеки

- **Kotlin** + **Coroutines** + **Flow**
- **AndroidX**: ViewModel, Navigation (NavController), ViewPager2
- **Room** – локальная база данных
- **Retrofit** + **OkHttp** – сетевой слой
- **Hilt** – Dependency Injection
- **Material Components** – стили и Bottom Sheet

---

## 🧠 ViewModel

- **HabitsListViewModel**
    - Загрузка списка привычек
    - Обработка отметки выполнения
    - Хранение текущих фильтров/сортировок
  
- **HabitEditViewModel**
    - Создание/редактирование привычки
    - Валидация полей
    - Отправка данных в репозиторий

Обе ViewModel расположены в `presentation` и получают данные через use-cases из `domain`.

---