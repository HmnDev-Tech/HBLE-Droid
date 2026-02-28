# BleDroid

Android BLE Advertising toolkit built with Kotlin and Jetpack Compose.

---

## 🇬🇧 English

## 📱 Overview

**BleDroid** is an Android application that demonstrates advanced usage of **Bluetooth Low Energy (BLE) advertising**.

The project provides multiple BLE payload generators and allows testing how nearby devices react to different advertising packets (Fast Pair, Swift Pair, wearable popups, etc.).

This project is intended for:

* Educational purposes
* BLE protocol research
* Android Bluetooth experimentation
* Security and device behavior analysis

⚠️ **Important:** This project is for research and educational purposes only. Use responsibly and comply with local laws and regulations.

---

## 🚀 Features

* BLE Advertising Engine
* Custom BLE packet generator
* Multiple predefined generators:

  * Fast Pair
  * Swift Pair
  * Samsung Buds simulation
  * Samsung Watch simulation
  * Apple-style device popup generator
  * Custom packet builder
* Foreground service for background advertising
* Jetpack Compose UI
* Material 3 design system
* Modular architecture

---

## 🏗 Architecture

```
app/
 ├── engine/        → BLE advertising logic
 ├── generators/    → BLE payload generators
 ├── models/        → Data models
 ├── helpers/       → Utilities (hex tools, etc.)
 ├── service/       → Foreground BLE service
 ├── ui/            → Compose UI layer
```

Architecture principles:

* MVVM pattern
* State-driven UI
* Separation of BLE engine and UI
* Modular packet generators

---

## 🛠 Tech Stack

* Kotlin
* Android SDK
* Bluetooth Low Energy (BLE)
* Jetpack Compose
* Material 3
* Gradle Kotlin DSL

---

## 📦 Installation

### Requirements

* Android Studio (latest recommended)
* Android device with BLE support
* Minimum SDK supported by project configuration

### Build

```bash
git clone https://github.com/your-username/BleDroid.git
cd BleDroid
./gradlew assembleDebug
```

Or open the project directly in Android Studio and run it on a real device.

---

## 🔒 Permissions

The app requires:

* Bluetooth
* Bluetooth Advertise
* Bluetooth Connect
* Location (required by Android for BLE scanning/advertising)

---

## 🧪 Usage

1. Launch the app.
2. Select a BLE generator mode.
3. Start advertising.
4. Observe nearby device behavior.

Always test in a controlled environment.

---

## 📄 License

Specify your license here (MIT / Apache 2.0 / etc.)

---

---

# 🇷🇺 Русская версия

## 📱 Обзор

**BleDroid** — это Android-приложение, демонстрирующее продвинутое использование **Bluetooth Low Energy (BLE) advertising**.

Проект предоставляет набор генераторов BLE-пакетов и позволяет исследовать реакцию устройств поблизости на различные advertising-пакеты (Fast Pair, Swift Pair, устройства носимой электроники и т.д.).

Проект предназначен для:

* Образовательных целей
* Исследования BLE-протокола
* Экспериментов с Bluetooth на Android
* Анализа поведения устройств

⚠️ **Важно:** Используйте проект ответственно и в соответствии с законодательством вашей страны.

---

## 🚀 Возможности

* BLE Advertising Engine
* Генерация кастомных BLE-пакетов
* Преднастроенные генераторы:

  * Fast Pair
  * Swift Pair
  * Эмуляция Samsung Buds
  * Эмуляция Samsung Watch
  * Генератор всплывающих окон Apple-устройств
  * Кастомный конструктор пакетов
* Foreground Service для фоновой работы
* UI на Jetpack Compose
* Material 3 дизайн
* Модульная архитектура

---

## 🏗 Архитектура

```
app/
 ├── engine/        → Логика BLE advertising
 ├── generators/    → Генераторы BLE-пакетов
 ├── models/        → Модели данных
 ├── helpers/       → Утилиты (hex и др.)
 ├── service/       → Foreground BLE сервис
 ├── ui/            → UI слой на Compose
```

Принципы:

* MVVM
* State-driven UI
* Разделение BLE-движка и UI
* Расширяемые генераторы пакетов

---

## 🛠 Технологии

* Kotlin
* Android SDK
* Bluetooth Low Energy (BLE)
* Jetpack Compose
* Material 3
* Gradle Kotlin DSL

---

## 📦 Установка

### Требования

* Android Studio (рекомендуется последняя версия)
* Android-устройство с поддержкой BLE
* Минимальный SDK согласно конфигурации проекта

### Сборка

```bash
git clone https://github.com/your-username/BleDroid.git
cd BleDroid
./gradlew assembleDebug
```

Или откройте проект в Android Studio и запустите на реальном устройстве.

---

## 🔒 Разрешения

Приложению требуются:

* Bluetooth
* Bluetooth Advertise
* Bluetooth Connect
* Location (требуется Android для работы BLE)

---

## 🧪 Использование

1. Запустите приложение.
2. Выберите режим генерации BLE.
3. Запустите advertising.
4. Наблюдайте реакцию устройств поблизости.
