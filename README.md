# Tracky

A clean, sleek calorie and macro tracker for Android with chat-style logging.

## Features

- Chat-style logging via text or photo
- Dataset-first nutrition/workout resolution
- Clear 7-day dashboard with daily totals
- Weight tracking with time range chart
- Transparent entry analysis

## Tech Stack

### Android App
- Kotlin + Jetpack Compose (Material 3)
- Room database with FTS for local dataset search
- Hilt for dependency injection
- CameraX for photo capture
- Vico for charts
- Retrofit + OkHttp for networking

### Backend
- Cloudflare Workers (TypeScript)
- Gemini API for parsing (text/photo analysis)
- USDA FoodData Central API for nutrition data
- Workers KV for caching

## Project Structure

```
Tracky/
├── app/                          # Android app module
│   ├── src/main/java/com/tracky/app/
│   │   ├── data/                 # Data layer
│   │   │   ├── local/            # Room database, entities, DAOs
│   │   │   ├── remote/           # Retrofit API, DTOs
│   │   │   └── repository/       # Repository implementations
│   │   ├── domain/               # Domain layer
│   │   │   ├── model/            # Domain models
│   │   │   └── usecase/          # Use cases/interactors
│   │   ├── di/                   # Hilt modules
│   │   └── ui/                   # Presentation layer
│   │       ├── theme/            # TrackyTheme, tokens, typography
│   │       ├── components/       # Reusable Tracky* components
│   │       ├── navigation/       # Navigation setup
│   │       └── screens/          # Feature screens
│   └── build.gradle.kts
├── backend/
│   └── worker/                   # Cloudflare Worker
│       ├── src/index.ts          # Worker entry point
│       ├── wrangler.toml         # Wrangler config
│       └── package.json
└── build.gradle.kts              # Root build config
```

## Setup

### Android App

1. Open the project in Android Studio
2. Sync Gradle
3. Update `BACKEND_URL` in `app/build.gradle.kts` with your deployed worker URL
4. Run on device/emulator

### Backend Worker

1. Navigate to `backend/worker`
2. Run `npm install`
3. Set up secrets:
   ```bash
   wrangler secret put GEMINI_API_KEY
   wrangler secret put USDA_API_KEY
   ```
4. Create KV namespace:
   ```bash
   wrangler kv:namespace create USDA_CACHE
   ```
5. Update `wrangler.toml` with your KV namespace IDs
6. Deploy: `npm run deploy`

## Development

### Local worker development
```bash
cd backend/worker
npm run dev
```

### Android development
Open in Android Studio and run on device/emulator.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /health | Health check |
| POST | /log/food | Parse food from text/image |
| POST | /resolve/food | Resolve food items to nutrition |
| POST | /log/exercise | Parse exercise description |
| POST | /resolve/exercise | Calculate exercise calories |
| POST | /narrative | Generate analysis narrative |

## License

Private - Personal use only.
