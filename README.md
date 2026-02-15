# 🍎 Tracky

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white) ![Cloudflare Workers](https://img.shields.io/badge/Cloudflare_Workers-F38020?style=for-the-badge&logo=cloudflare&logoColor=white) ![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)

**Tracky** is a premium, AI-powered calorie and macro tracker that feels as natural as texting a friend. Forget clunky inputs and endless searching—just snap a photo or type what you ate, and Tracky handles the rest.

---

## ✨ Features

### 🍎 Effortless Logging
- **Chat-Based Interface**: Log your meals and workouts just by texting naturally (e.g., "Ate a banana and 2 eggs").
- **Visual Food Logging**: Snap a photo 📸 of your meal. Tracky's **Retrieval-Driven AI** identifies food with precision using a verified canonical database—no more random guesses.
- **Manual Control**: Full support for manual entry of calories, macros, duration, and intensity when you need precision.
- **Auto-Detection**: The app automatically figures out if you're logging food, exercise, or just chatting.
- **Smart Drafts**: Review and edit every entry before it's saved. You're always in control.
- **Data Flywheel**: The system learns from every interaction. Your confirmed entries become the "Gold Standard" for future accuracy, creating a personalized and ever-improving database.
- **Plausibility Guardrails**: Built-in physics checks ensure nutritional data makes mathematical sense (e.g., matching calories to [Carbs*4 + Protein*4 + Fat*9]).

### 📊 Smart Dashboard
- **6-Day Strip**: Swipe through your week to track consistency with smooth sliding transitions.
- **Live Progress**: Green/Red daily indicators and filling rings for Calories, Protein, Carbs, and Fat.
- **Dynamic Messaging**: Calorie cards update with encouraging messages based on your progress.
- **Premium Feel**: Experience rich haptics and sound effects as you interact.
- **Quick Actions**: Edit daily goals on the fly.

### 🔍 Deep Insights & Control
- **Full Breakdown**: Tap any entry for granular nutrient data.
- **Re-Analyze**: Tap any chat message to edit the text and completely re-process the entry with new context.
- **History**: Swipe-to-delete flows for easy management.

### ⚖️ Weight Management
- **Interactive Charts**: Visualize trends over Daily, Weekly, Monthly, or All Time ranges.
- **Goal Dynamics**: See exactly how much you have left to lose (or gain) to hit your target.

---

## 🛠️ Tech Stack

### Android App
*   **Language**: Kotlin
*   **UI**: Jetpack Compose (Material 3)
*   **Architecture**: MVVM + Clean Architecture
*   **Dependency Injection**: Hilt
*   **Local Data**: Room Database + FTS4 (dataset search)
*   **Network**: Retrofit + OkHttp
*   **Charts**: Vico

### Backend (Serverless)
*   **Platform**: Cloudflare Workers (TypeScript)
*   **AI**: Gemini Pro / GPT-4o-mini (Text & Vision)
*   **Data**: AI-first
*   **Cache**: Workers KV

---

## 📂 Project Structure

```bash
Tracky/
├── app/                          # Android Application
│   ├── src/main/java/com/tracky/app/
│   │   ├── data/                 # Repositories, API, Room DB
│   │   ├── domain/               # Use cases, Models, Resolvers
│   │   ├── di/                   # Hilt Modules
│   │   └── ui/                   # Jetpack Compose Screens & Components
├── backend/
│   └── worker/                   # Cloudflare Worker Source
└── build.gradle.kts              # Build Configuration
```

---

## 🚀 Getting Started

### Android App
1.  **Clone the repo**: `git clone https://github.com/keithruezyl1 /tracky.git`
2.  **Open** in Android Studio Ladybug or newer.
3.  **Sync** Gradle project.
4.  **Configure**: Update `BACKEND_URL` in `app/build.gradle.kts` if deploying your own backend.
5.  **Run**: Press Play ▶️ to launch on emulator or device.

### Backend Worker
1.  Navigate to `backend/worker`.
2.  Run `npm install`.
3.  **Secrets Setup**:
    ```bash
    wrangler secret put AI_FIRST_API_KEY
    ```
4.  **Deploy**: `npm run deploy`

---

## 📜 License

Private - Personal use only.
