# EduApp - Premium Quiz Application

EduApp is a modern, polished Android quiz application built using Jetpack Compose. It features a sleek "Frosted Glass" design, dynamic difficulty levels, and a persistent leaderboard system.

## 🚀 Features

-   **Dynamic Difficulty Levels**: Choose between Explorer, Challenger, and Champion modes.
-   **Customizable Gameplay**: Configure the number of questions per round (6, 10, or 12) via the Settings.
-   **Persistent Leaderboard**: High scores are saved to a local Room Database and displayed in a real-time leaderboard.
-   **Audio & Haptic Feedback**: Optional sound effects and vibration feedback for correct/incorrect answers.
-   **Modern UI/UX**: 
    -   Deep Indigo to Soft Plum vertical gradients.
    -   Translucent "Frosted Glass" cards.
    -   Responsive layout with vertical scrolling for smaller screens.
    -   Material 3 components and theming.
-   **Robust Architecture**: 
    -   Full state persistence across screen rotations using `rememberSaveable` and `ViewModel`.
    -   Clean MVVM architecture.
    -   Safety first: Destructive actions like resetting the leaderboard require confirmation.

## 🛠 Tech Stack

-   **Language**: [Kotlin](https://kotlinlang.org/)
-   **UI Framework**: [Jetpack Compose](https://developer.android.com/compose)
-   **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
-   **Navigation**: Jetpack Navigation Compose
-   **Asynchronous Processing**: Coroutines & Flow
-   **State Management**: ViewModel & StateFlow

## 📂 Project Structure

-   `database/`: Entity, DAO, and Database configurations for Room.
-   `viewmodel/`: Business logic and state management for the quiz rounds.
-   `screen/`: Composable screens (Landing, Game, Result, Settings).
-   `helper/`: Utility functions and asset loading helpers.

## 🧪 Testing

The project includes both Unit and Instrumented tests:
-   **AppViewModelTest**: Verifies score calculation and answer validation logic.
-   **AppDaoTest**: Ensures database integrity for score storage and retrieval.

## 📦 How to Run

1.  Clone the repository.
2.  Open the project in **Android Studio (Ladybug or newer)**.
3.  Sync the project with Gradle files.
4.  Run the app on an emulator or a physical device (API 26+).

---
*Developed as part of an Android Development Assessment.*
