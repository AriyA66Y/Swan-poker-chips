# Poker Chip Manager & Table Tracker

[![Version](https://img.shields.io/badge/version-1.0.1-gold.svg)](https://github.com/)
[![Platform](https://img.shields.io/badge/platform-Android-brightgreen.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(M3)-blue.svg)](https://developer.android.com/jetpack/compose)

A poker chip manager and live table tracker built for Android with Jetpack Compose and Material 3. Designed for home games, poker clubs, and cash games, it eliminates manual math, chip-counting disputes, and complex side pot calculations.

---

## Key Features

### 1. Complete Turn-Based Betting Engine
- **Full Street Tracking:** Preflop, Flop, Turn, River, and Showdown phases.
- **Smart Action Controls:** Context-aware actions including **Fold**, **Check**, **Call** (with exact diff computation), **Bet**, **Raise**, and **All-In**.
- **Interactive Raise Panel:** Includes an interactive slider and quick preset buttons:
  - Minimum Raise (Min)
  - 2x Big Blind
  - Half Pot (½ Pot)
  - Full Pot
  - All-In (Max)

### 2. Automated Multi-Pot & Side Pot System
- Automatically calculates and splits chips into **Main Pot** and **Side Pots** when one or more players go all-in with unequal stacks.
- Tracks player eligibility per pot according to official Texas Hold'em rules.
- Dedicated **Showdown Dialog** for selecting single or multiple winners per pot with automatic split pot distribution.

### 3. Comprehensive Player Management
- Add new players with personalized names, color avatars, and customizable initial buy-ins.
- Seat rotation with automatic **Dealer (D)**, **Small Blind (SB)**, and **Big Blind (BB)** position markers.
- **Re-buy Support:** Add chips to any player's stack while keeping a complete financial ledger.
- **Manual Chip Adjustments (+/-):** Correct chip counts at any time with customizable reason logs.

### 4. Visual Profit & Loss (P/L) Analytics
- **Bi-Directional P/L Chart:** Visual green/red bar chart highlighting net profit/loss and Return on Investment (ROI %) for every player.
- **Table Balance Verification:** Real-time audit tool ensuring Total Buy-ins equal Total Chips in play (zero-sum integrity).
- **Hand History Ledger:** Detailed, expandable logs of every completed hand with timestamps, total pot sizes, and individual player chip deltas.

### 5. Game Templates
- Save current table configurations (players, stacks, blinds, settings) as reusable templates.
- Quickly restore, overwrite, or delete saved game templates.

### 6. Internationalization & Bilingual Support (English / فارسی)
- Full localization in **English** and **Persian (Farsi)**.
- Dynamic layout direction switching (**LTR** for English, **RTL** for Persian).
- Language toggle available inside the Game Settings dialog.

### 7. Reliable Offline Persistence
- Powered by Android **Room Database (SQLite)**.
- Real-time automatic state persistence ensures ongoing games and stats remain safe even across app restarts.

---

## Tech Stack & Architecture

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles
- **State Management:** Kotlin Coroutines & `StateFlow`
- **Local Persistence:** Room Database (Room KSP) + JSON State Serializer
- **Compatibility:** Android 8.0 (API 26) and above

---

## Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- JDK 17 or higher
- Android SDK with Platform 35

### Building the Project
Clone the repository and build the debug APK using Gradle:

```bash
gradle assembleDebug
```

The generated APK will be available in:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## How to Play / Quick Guide

1. **Setup Table:** Add players with their buy-in amount. Blinds and currency symbol can be adjusted from the **Settings (⚙)** menu.
2. **Start Hand:** Tap **"Start Hand"**. Blinds are automatically posted.
3. **Betting Rounds:** Follow the turn indicator. Use Fold, Check, Call, or Bet/Raise to progress through Preflop, Flop, Turn, and River.
4. **Showdown:** When action completes on the River (or when players are all-in), the Showdown screen opens. Tap the winning player(s) for each pot and confirm to award chips.
5. **Review Stats:** Check the **Analytics** tab anytime to view player net profit/loss, ROI %, and hand history.

---

## License

This project is licensed under the MIT License.
