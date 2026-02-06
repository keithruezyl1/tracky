# Tracky — One-Shot PRD (Calorie + Macro + Weight Tracker) **with Required Plugins/Libraries**

**Product name:** Tracky
**Document version:** 1.1 (One-shot build spec + stack + dependencies)
**Date:** 2026-02-04
**Platform focus:** **Android-first**
**Inspiration:** Journable’s “chat to track” approach (text/photos for logging meals & exercise).
**Design system reference:** Tracky Design Guidelines (v1.2) — tokens + components are the source of truth.

---

## 0) Tech stack overview (Android-first, premium UX)

### Architecture (recommended)

* **Frontend:** Kotlin + Jetpack Compose (Material 3 themed to Tracky tokens)
* **Local database:** SQLite via **Room** (+ **FTS** for dataset-first search)
* **Backend:** **Cloudflare Workers** (TypeScript) as thin proxy/orchestrator
* **LLM:** **OpenAI** (text + vision)
* **Food fallback:** USDA FoodData Central API
* **Exercise dataset:** MET values (Compendium-based)
* **Charts:** Vico (Compose-native)
* **Images/camera:** CameraX + Coil

---

## 0.1 Plugins / Libraries / Extensions (MUST INSTALL)

### Android Gradle Plugins

* `com.android.application`
* `org.jetbrains.kotlin.android`
* `org.jetbrains.kotlin.plugin.compose`
* `kotlin-kapt`
* `com.google.dagger.hilt.android`
* `org.jetbrains.kotlin.plugin.serialization` *(recommended)*

Optional (only if you want crash reporting):

* `com.google.gms.google-services`
* `com.google.firebase.crashlytics`

### Android Dependencies (Core)

**Compose / UI**

* `androidx.compose.ui:ui`
* `androidx.compose.foundation:foundation`
* `androidx.compose.animation:animation`
* `androidx.compose.material3:material3`
* `androidx.compose.material:material-icons-extended`
* `androidx.activity:activity-compose`
* `androidx.lifecycle:lifecycle-runtime-compose`
* `androidx.lifecycle:lifecycle-viewmodel-compose`

**Navigation**

* `androidx.navigation:navigation-compose`

**Premium polish**

* `com.google.accompanist:accompanist-systemuicontroller`
* `com.google.accompanist:accompanist-permissions`
* `com.google.accompanist:accompanist-navigation-animation`

**Images**

* `io.coil-kt:coil-compose`

**Camera**

* `androidx.camera:camera-core`
* `androidx.camera:camera-camera2`
* `androidx.camera:camera-lifecycle`
* `androidx.camera:camera-view`

**Charts**

* `com.patrykandpatrick.vico:core`
* `com.patrykandpatrick.vico:compose`
* `com.patrykandpatrick.vico:compose-m3`

**Async + date/time**

* `org.jetbrains.kotlinx:kotlinx-coroutines-android`
* `org.jetbrains.kotlinx:kotlinx-datetime`

**DI**

* `com.google.dagger:hilt-android`
* `com.google.dagger:hilt-compiler`
* `androidx.hilt:hilt-navigation-compose`

**Database**

* `androidx.room:room-runtime`
* `androidx.room:room-ktx`
* `androidx.room:room-compiler` *(kapt)*
* *(FTS is built-in via Room entities)*

**Preferences**

* `androidx.datastore:datastore-preferences`

**Networking**

* `com.squareup.okhttp3:okhttp`
* `com.squareup.okhttp3:logging-interceptor` *(debug)*
* `com.squareup.retrofit2:retrofit`
* `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter`

**Serialization**

* `org.jetbrains.kotlinx:kotlinx-serialization-json`

**Logging**

* `com.jakewharton.timber:timber`

Optional security hardening:

* `androidx.security:security-crypto`

Optional animations:

* `com.airbnb.android:lottie-compose`
* `com.valentinilk.shimmer:compose-shimmer`

---

### Backend (Cloudflare Workers) Dependencies

**Tooling**

* `wrangler`
* `typescript`

**Runtime deps**

* `zod` *(request/response validation)*
* Workers KV *(Cloudflare product)*

Backend integrates:

* OpenAI API
* USDA FoodData Central API

---

## 1) Product overview

### 1.1 Problem statement

Most calorie tracking apps require too many taps (search, servings, manual entry), reducing adherence.

### 1.2 Solution

Tracky is a **clean, sleek** calorie & macro tracker with:

* Chat-style logging (text or photo)
* Dataset-first nutrition/workout resolution
* Clear 7-day dashboard + daily totals
* Weight tracking + time range chart
* Transparent entry analysis (tap entry → full breakdown)

### 1.3 Target user

Single-user personal tracker (you).

### 1.4 Product principles

1. Low friction
2. Clean UI (token-only)
3. User control (confirm/edit always)
4. Transparency (show provenance + reasoning)

---

## 2) Goals, non-goals, and success metrics

### 2.1 Goals (MVP)

* Onboarding captures height/weight/target + BMI auto-calc
* Daily calorie goal + macro % split (must total 100%)
* Log food/exercise via chat (text/photo)
* Dataset-first nutrition/workout estimation, online fallback
* Entry detail view shows full analysis
* Weight tracker with chart + add/edit/delete entries

### 2.2 Non-goals (MVP)

* Social/community
* Meal plans
* Medical advice
* Wearable integrations

### 2.3 Success metrics (personal)

* Days logged per week
* % days meeting calorie goal
* % entries requiring manual correction

---

## 3) Core user flows

### 3.1 Onboarding (first launch)

1. Units selection (metric/imperial)
2. Height + current weight + target weight
3. BMI calculated and shown
4. Set daily goals:

   * calorie limit/day
   * macro % split (carbs/protein/fat) = 100%
5. Finish → Today dashboard

**BMI formula:** BMI = weight(kg) / height(m)².

### 3.2 Daily logging (happy path)

1. User opens app → Today dashboard
2. User logs meal/exercise via chat:

   * type text OR take photo OR upload
3. OpenAI parses into structured draft
4. Resolver matches dataset-first, computes totals
5. Draft shown → user confirms
6. Totals update; entry appears in feed

### 3.3 Entry deep dive

Tap entry → Entry Detail:

* item breakdown
* calories/macros
* analysis narrative
* nutrition facts
* actions: edit, adjust, change time/date, save, delete

### 3.4 Weight tracking

Weight Tracker screen:

* current weight vs target
* weight entries list (CRUD)
* chart (week/month/year/all)

---

## 4) Functional requirements (MVP)

### 4.1 Profile & goals

**FR-P1 Profile fields**

* height_cm
* current_weight_kg
* target_weight_kg
* unit preference
* timezone

**FR-P2 Daily goals**

* calorie_goal_kcal
* macro split %:

  * carbs_pct
  * protein_pct
  * fat_pct
  * must sum to 100

**Macro grams preview calculation**

* carbs/protein: 4 kcal/g
* fat: 9 kcal/g

### 4.2 Calorie goal calculator (optional but recommended)

* Use Mifflin-St Jeor to estimate BMR
* Activity multiplier selection
* Deficit/surplus selection
* Output suggested calorie goal (user can override)

### 4.3 Main dashboard

**FR-D1 7-day strip**

* shows last 7 days
* highlights selected day (today default)

**FR-D2 Daily summary cards**
Calories card:

* food kcal
* exercise kcal
* remaining kcal

Remaining formula:
`remaining = goal − food + exercise`

Macros card:

* carbs/protein/fat consumed vs target grams

### 4.4 Logging interface (chat-style)

**FR-L1 Message types**

* user text
* user image
* system draft card
* assistant clarifying prompts

**FR-L2 Draft → confirm**

* always show draft with:

  * items
  * totals
  * provenance
  * confidence
* actions:

  * confirm
  * edit
  * cancel

**FR-L3 Offline**

* allow manual entry offline
* queue AI draft requests if needed

---

## 5) Dataset-first AI pipeline (OpenAI integration)

### 5.1 Policy (hard rule)

1. local dataset
2. USDA FDC fallback
3. other online sources only if explicitly approved later

### 5.2 OpenAI responsibilities

* parse text/photo into structured items
* propose candidate dataset queries
* generate analysis narrative **without inventing nutrition values**

### 5.3 Resolver responsibilities (deterministic)

* match foods/exercises against local dataset via FTS
* compute calories/macros deterministically
* only call USDA if dataset misses

### 5.4 Provenance tracking (required)

Each item stores:

* source: dataset | usda_fdc | user_override
* matched label/id
* confidence

---

## 6) Food estimation engine

### 6.1 Primary online fallback: USDA FoodData Central

* Use API endpoints `/foods/search` and `/food/{fdcId}`
* Cache results in Worker KV

### 6.2 Portion handling

* support grams
* support household units when mapping exists
* low confidence → ask clarifying questions

---

## 7) Exercise estimation engine

### 7.1 MET method

1 MET ≈ 1 kcal/kg/hour
Calories burned:
`kcal = MET × weight_kg × hours`

### 7.2 Mapping

* activity label → MET entry in dataset
* ambiguous → ask user to select

---

## 8) Entry storage + editing

### Required actions

* Edit entry
* Adjust calories/macros (override)
* Change date/time
* Save entry template
* Delete

---

## 9) Weight tracker

### Weight entry CRUD

* add/edit/delete weight entries

### Chart requirements

* time ranges: week/month/year/all
* line series for entries
* dashed target weight line

---

## 10) Data model (Room schema)

### Entities

* UserProfile
* DailyGoal (effective_from_date)
* FoodEntry + FoodItem
* ExerciseEntry
* WeightEntry
* SavedEntry
* ChatMessage
* FoodsDataset + FoodsFTS + Synonyms

---

## 11) Non-functional requirements

### Performance

* Today loads from local DB
* AI draft shows progress UI

### Privacy

* photos sent only for analysis
* optional “store photos locally” setting

### Accessibility

* dynamic font scaling
* touch targets ≥ 48dp

---

## 12) Acceptance criteria (must-pass)

* Onboarding enforces macro sum = 100
* BMI computed correctly
* Draft confirmation required for all AI logs
* Dataset-first resolution enforced
* USDA fallback works when dataset misses
* Weight chart + time ranges function correctly

---

## Appendix A — Required formulas

**BMI:** weight(kg) / height(m)²
**Macros:** carbs/protein 4 kcal/g; fat 9 kcal/g
**Exercise kcal:** MET × weight_kg × hours
**BMR:** Mifflin-St Jeor

---

