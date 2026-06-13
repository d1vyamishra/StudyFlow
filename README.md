# 🌌 Study Flow — Bento-Stylized Study Tracker

[![Build Android APK](https://github.com/d1vyamishra/StudyFlow/actions/workflows/android.yml/badge.svg)](https://github.com/d1vyamishra/StudyFlow/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-blue.svg?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3 Android](https://img.shields.io/badge/Design-Material_3-blueviolet.svg?style=flat-square)](https://m3.material.io/)
[![Room Database](https://img.shields.io/badge/Database-SQLite_Room-cyan.svg?style=flat-square)](https://developer.android.com/training/data-storage/room)

**Study Flow** is an ultra-modern, Bento-grid inspired study management companion designed down to the pixel for elite focus and analytical clarity. Crafted with native Android Jetpack Compose, it features high-tactility interactive blocks, a beautiful color-accurate **Sky Blue** theme, a dynamic Light/Dark mode toggle, real-time persistence, and custom branding.

---

## 🎨 Visual Identity

| Space Midnight (Dark Theme) | Pure Sky Slate (Light Theme) |
|---|---|
| ![Dark Mockup](https://i.ibb.co/F492C7Qt/Gemini-Generated-Image-tbtacztbtacztbta.png) | *Modern Sky 500 Primary Accents* |

### Key Aesthetic Pillar
- **Bento Card Layouts**: Inspired by sophisticated card-grid dashboards, utilizing clean rounded corners, dynamic padding, and crisp inner content spacing.
- **Brand Identity Logo**: Equipped with a professional, custom-generated asset embedded directly into the layout.
- **Palette**: Focuses on professional, calming **Sky Blue (`#0EA5E9`)** blended with deep **Space Midnight (`#0B1329`)** and **Slate White (`#F8FAFC`)**.

---

## 🔥 Features Summary

- ⏱️ **Triple-Stream Session Tracking**: Segment your focus effortlessly across:
  - **Video Lectures** (Indigo/Sky Accent)
  - **Self-Study** (Steel Blue/Light Accent)
  - **Question Practice** (Vibrant Cyan Accent)
- 🎛️ **Responsive Tactile Controls**: Toggle timers dynamically on a whim, or tweak sessions on the fly with responsive `+5m`, `-5m`, and `+15m` precision adjustments.
- 🌓 **Dynamic Lighting Switch**: Switch between a high-contrast dark space dashboard and a lightweight light theme in a single tap.
- 📊 **Bento Capsule Analytics**: View your study history as relative-height capsule charts, complete with daily averages, consecutive streaks, and automatic progress milestone bars.
- 📁 **Archive Logging**: Modify, review, or safely clear past sessions directly in your local records cache. Included with full validation safeguards.
- 🔒 **Zero Telemetry / Fully Local**: Powered entirely by transactional SQL SQLite via SQLite Room DB. Your study records stay private on your local storage.

---

## 🛠️ The Tech Stack

*   **Runtime UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3).
*   **Database Schema**: [Room State Persistence](https://developer.android.com/training/data-storage/room) with multi-index transactional histories.
*   **Architecture**: MVVM model (`ViewModel` + `StateFlow` + constructor-injected `Repository` pattern).
*   **Asynchronous Engine**: Kotlin Coroutines & Flow structures driving state notifications without blocking layouts.
*   **Automation Framework**: Custom Github Action CI Pipeline (`android.yml`) compiling debug APK builds automatically on codebase pushes.

---

## 🚀 Getting Started

### Prerequisites
- JDK 17
- Android Studio Ladybug (or newer versions)
- Android SDK (targetSdk: 35, minSdk: 26)

### Clone & Assemble Locally
```bash
# Clone this archive
git clone https://github.com/d1vyamishra/StudyFlow/.git
cd StudyFlow

# Compile and verification check
gradle assembleDebug
```

---

## 📂 Project Organization

```text
app/src/main/java/com/example/
 ├── data/
 │    ├── StudyDatabase.kt      # Main Database structure
 │    ├── StudyRecord.kt        # Persistent Entity model
 │    ├── StudyDao.kt           # Data access interface layer
 │    └── StudyRepository.kt    # Abstracted Transaction Manager
 ├── ui/
 │    ├── HomeScreen.kt         # Live Bento Dashboard Engine
 │    ├── AnalyticsScreen.kt    # Canvas stack-charts & streak gauges
 │    ├── HistoryScreen.kt      # Archive timeline lists
 │    ├── StudyTrackerApp.kt    # Compose Navigation architecture
 │    ├── StudyViewModel.kt     # Shared reactive state engine
 │    └── theme/
 │         ├── Color.kt         # Sky Blue Palette (Light & Dark variant specs)
 │         ├── Theme.kt         # Material Theme builder
 │         └── Type.kt          # M3 Typography configurations
 └── MainActivity.kt            # Core entry lifecycle hook
```

---

## ❤️ Signature Branding

This study flow experience was meticulously crafted down to the last alignment:

```yaml
Made By: "@v1shal.irl"
Status: Fully Functional, Highly Stylized, Safe SQLite Active
```
