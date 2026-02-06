# Tracky — Design Guidelines (v1.2) + Required Plugins/Libraries (Android / Jetpack Compose, Premium UX)

This is the **complete design guideline** (token system + component rules + screen patterns) with an explicit **install list** (plugins/libraries) required to implement it on Android.

---

## 0) Required plugins, libraries, and extensions

### 0.1 Gradle plugins (required)

* `com.android.application`
* `org.jetbrains.kotlin.android`
* `org.jetbrains.kotlin.plugin.compose`
* `kotlin-kapt`

### 0.2 Gradle plugins (recommended for premium DX)

* `com.google.dagger.hilt.android` *(DI; improves maintainability)*

### 0.3 Compose UI foundation (required)

* `androidx.compose.ui:ui`
* `androidx.compose.ui:ui-tooling-preview`
* `androidx.compose.foundation:foundation`
* `androidx.compose.animation:animation`
* `androidx.compose.material3:material3`
* `androidx.compose.material:material-icons-extended`
* `androidx.activity:activity-compose`
* `androidx.lifecycle:lifecycle-runtime-compose`
* `androidx.lifecycle:lifecycle-viewmodel-compose`

### 0.4 Navigation + motion (premium UX)

* `androidx.navigation:navigation-compose`
* `com.google.accompanist:accompanist-navigation-animation`

### 0.5 System UI + permissions (premium polish)

* `com.google.accompanist:accompanist-systemuicontroller`
* `com.google.accompanist:accompanist-permissions`

### 0.6 Images (required for food photo UI)

* `io.coil-kt:coil-compose`

### 0.7 Camera (premium embedded camera experience)

* `androidx.camera:camera-core`
* `androidx.camera:camera-camera2`
* `androidx.camera:camera-lifecycle`
* `androidx.camera:camera-view`

### 0.8 Charts (Weight Tracker line graph)

* `com.patrykandpatrick.vico:core`
* `com.patrykandpatrick.vico:compose`
* `com.patrykandpatrick.vico:compose-m3`

### 0.9 Date/time (recommended)

* `org.jetbrains.kotlinx:kotlinx-datetime`

### 0.10 Optional premium microinteractions

* `com.airbnb.android:lottie-compose` *(optional)*
* `com.valentinilk.shimmer:compose-shimmer` *(optional)*

> Bottom sheets: use **Material 3 `ModalBottomSheet`** (already included in `androidx.compose.material3:material3`).

---

## 1) Reference-derived visual direction (locked)

### 1.1 Consistent characteristics to keep

* **Clean white surfaces** with subtle borders/dividers
* **Soft corners** (rounded cards, rounded inputs, pill chips)
* **Minimal accent usage** (one accent per component)
* **Readable hierarchy**: bold titles, quiet metadata rows
* **Sheet-driven edits**: bottom sheets for macro distribution + entry actions

### 1.2 Elements to constrain for cleanliness

* Strong gradients or warm palettes must be **contextual only** (not global UI chrome)
* Avoid heavy shadows; use **borders** + subtle elevation

---

## 2) Locked system rules (non-negotiables)

1. **Token-only colors** (no ad-hoc hex values in UI code).
2. **Token-only radii** (no random rounding).
3. **8-pt spacing grid** everywhere.
4. **One accent per component**.
5. **Primary actions** use Brand Blue (solid) — gradients are allowed only for hero moments, not standard CTAs.
6. Text on tinted/colored surfaces must maintain strong contrast.

---

## 3) Color system (canonical tokens)

> Screenshot colors can’t be confirmed as exact due to device/compression; these are the **canonical locked tokens** to enforce consistency.

### 3.1 Usage distribution (screen-level)

* **Neutrals: 80%**
* **Brand Blue: 15%**
* **Accents: 5%**

### 3.2 Core palette

#### Neutrals

* `Neutral/0` `#FFFFFF` (surface)
* `Neutral/50` `#F7F8FA` (app background)
* `Neutral/100` `#EEF1F5` (subtle surface, separators)
* `Neutral/200` `#E2E6EC` (borders/dividers)
* `Neutral/500` `#8B93A3` (tertiary text)
* `Neutral/700` `#556070` (secondary text)
* `Neutral/900` `#0B0D11` (primary text)

#### Brand

* `Brand/Primary` `#2E6BB5` (primary CTAs, selected states)
* `Brand/Tint` `#EAF2FF` (info surface, header strips)
* `Brand/Deep` `#1E4D86` (pressed/active variants)

#### Semantic accents

* `Success` `#34C759`
* `Warning` `#FF9500`
* `Error` `#FF3B30`

### 3.3 Theme mapping

**Light (default)**

* Background: `Neutral/50`
* Surface: `Neutral/0`
* Primary text: `Neutral/900`
* Secondary text: `Neutral/700`
* Border: `Neutral/200` (1dp)

(Dark mode optional; can be added later.)

---

## 4) Typography (locked)

### 4.1 Font families

* **UI Body:** system font (Roboto)
* **Display/Headlines:** if using a premium display font, bundle it in `res/font/` (no extra dependency required)

### 4.2 Type scale

* Display L: 28/32 (screen titles)
* H1: 22/28 (section titles)
* H2: 18/24 (card titles)
* Body: 16/22
* Body S: 14/20
* Caption: 12/16
* Overline: 11/14 (tracking +6%)

### 4.3 Text rules

* Titles use `Neutral/900`
* Secondary uses `Neutral/700`
* Disabled uses `Neutral/500` at ~60% opacity

---

## 5) Layout grid, spacing, and shapes

### 5.1 Spacing tokens (8-pt grid)

Use only: **4, 8, 12, 16, 24, 32, 40, 48**

Defaults:

* Screen padding: **16**
* Card padding: **16**
* Section spacing: **24**
* Dense list spacing: **12**

### 5.2 Radius tokens

* XS: 8 (small elements)
* S: 12 (inputs, small cards)
* M: 14 (buttons default)
* L: 16 (cards default)
* XL: 24 (bottom sheets top corners)
* Pill: 999 (chips, segmented control)

---

## 6) Elevation, borders, dividers

### 6.1 Elevation

* Default cards: very subtle shadow (or none) + border
* Sheets: use shape + top handle + border

### 6.2 Borders/dividers

* Border thickness: **1dp**
* Color: `Neutral/200`

---

## 7) Core components (locked specs)

### 7.1 Buttons

**Primary**

* Height: 48
* Radius: 14
* Fill: `Brand/Primary`
* Text: `Neutral/0`
* Pressed: darken to `Brand/Deep` or add 8% black overlay

**Secondary**

* Height: 48
* Radius: 14
* Fill: transparent
* Border: 1dp `Brand/Primary` @ ~60–80%
* Text: `Brand/Primary`

**Tertiary**

* Text-only, `Brand/Primary`

### 7.2 Inputs

* Height: 48
* Radius: 14
* Fill: `Neutral/0`
* Border: 1dp `Neutral/200`
* Focus: border changes to `Brand/Primary`

### 7.3 Cards

**Base Card**

* Radius: 16
* Padding: 16
* Fill: `Neutral/0`
* Border: 1dp `Neutral/200`

**Info Card / Tinted**

* Fill: `Brand/Tint`
* Text remains `Neutral/900`

### 7.4 Chips (7-day strip, filters)

* Height: 28–32
* Radius: 10 (or pill)
* Default: `Neutral/0` with `Neutral/200` border
* Selected: fill `Brand/Tint`, text `Brand/Primary`

### 7.5 Progress bars (kcal + macros)

* Track: `Neutral/100`
* Fill: `Brand/Primary`
* Warning state: `Warning`
* Always pair with a numeric label (no color-only encoding)

### 7.6 Bottom sheets (macro distribution, entry actions)

Use Material 3 `ModalBottomSheet`:

* Radius (top): 24
* Drag handle: subtle capsule
* Sheet sections separated by dividers (`Neutral/200`)
* Primary action anchored at bottom when needed

### 7.7 Entry list row (food/exercise)

* Card container (radius 16)
* Top: title + kcal
* Middle: item list (food lines) or exercise details
* Bottom: macro mini-row (carb/protein/fat), timestamp, overflow menu

### 7.8 Icons

* Use Material Icons (outlined style where possible)
* Sizes: 16 / 20 / 24
* Default color: `Neutral/700`; selected: `Brand/Primary`
* Destructive icons: `Error`

---

## 8) Screen patterns (based on your references)

### 8.1 Onboarding / Initial setup

* Form pattern: stacked inputs with dividers
* Show BMI as a computed read-only row
* Macro % editor opens as a bottom sheet

### 8.2 Daily Goals screen

* Sections:

  * Calories goal (number input)
  * Macro distribution (summary row + “Edit” → sheet)
* Macro distribution sheet:

  * three sliders/steppers (carb/protein/fat %)
  * live total must equal 100 before enabling “Done”

### 8.3 Home / Day dashboard

* Top: 7-day strip (chips)
* Middle: two summary cards:

  * Calories: Food / Exercise / Remaining
  * Macros: Carbs / Protein / Fat (g vs target)
* Bottom: chat-style composer bar:

  * text input + camera + gallery + saved entries

### 8.4 Chat logging UI

* Bubbles: minimal (mostly flat surfaces)
* Draft card appears as a structured card in the thread:

  * itemized breakdown + totals + confirm/edit actions

### 8.5 Entry detail screen

* Summary card (kcal + macros)
* “Analysis” card
* Nutrition facts section as list/table with dividers
* Actions available via bottom sheet

### 8.6 Weight tracker

* Header: current vs target
* Chart: line series + dashed target line
* Range toggles: week/month/year/all
* Entries list: add/edit/delete

---

## 9) Implementation rules (to keep UI consistent)

* Build a small internal component kit:

  * `TrackyButtonPrimary`, `TrackyButtonSecondary`
  * `TrackyCard`, `TrackyChip`, `TrackyInput`
  * `TrackyBottomSheetScaffold`
  * `TrackyProgress`
* No direct use of raw `Button`, `Card`, `TextField` in feature screens except inside these wrappers.
* All colors/radii/spacing are referenced from `TrackyTokens`.

---

## 10) What this library set enables (mapping)

* **Premium bottom sheets**: Material3 `ModalBottomSheet`
* **Polished transitions**: Accompanist navigation animation
* **Status bar matching**: Accompanist system UI controller
* **Camera-first meal capture**: CameraX
* **Fast image rendering**: Coil
* **Clean weight charts**: Vico Compose
* **Date strip logic**: kotlinx-datetime

---

Additions to the Design Guidelines (v1.2)
A) What to avoid (hard “do not” list)

Visual noise

No random emojis in UI copy, labels, buttons, empty states, or analysis text.

No decorative icons unless they convey meaning (e.g., camera, edit, delete, bookmark/saved).

No gradient backgrounds behind dense text.

No multiple accents in one component (e.g., blue + green + orange simultaneously).

Inconsistency

No ad-hoc hex colors. Use tokens only.

No ad-hoc corner radii. Use radius tokens only.

No off-grid spacing (must follow 8-pt system).

No mixed icon styles (stick to one: Material outlined recommended).

No mixed typography (don’t introduce new font weights/sizes outside the scale).

Layout anti-patterns

Avoid heavy shadows; prefer borders + subtle elevation.

Avoid center-aligned body text (use left-aligned for readability).

Avoid long paragraphs in cards; chunk content into short rows/sections.

Copy anti-patterns

Avoid overly “chatty” UI microcopy.

Avoid ALL CAPS except Overline labels (11sp/14sp with tracking).

Avoid ambiguous CTAs (use “Save”, “Confirm”, “Edit”, “Delete”).

B) Smooth, slick animations (system-wide requirements)

You want “clean, smooth and slick animations all the way.” Implement these as global rules:

B1) Motion principles (locked)

Purposeful only: every animation must communicate state change (navigation, expand/collapse, draft creation, confirmation).

Short durations: generally 150–250ms for microinteractions; 250–350ms for screen transitions.

Use easing: no linear motion except progress indicators.

No bouncy overshoot unless explicitly used in one place (e.g., success check).

B2) Required animation behaviors by area

Navigation

Consistent enter/exit transitions across the app (fade + slight vertical slide).

Shared-element style is optional; if used, only for entry → entry detail (hero card expand).

Bottom sheets

Smooth snap + dim scrim fade.

Sheet content should animate in (subtle fade/slide) rather than appear abruptly.

Chat composer + draft lifecycle

“Drafting…” state: shimmer or subtle pulsing placeholder (optional).

Draft card: animate-in (fade + slide up).

Confirm action: quick success feedback (haptic + subtle scale/fade).

Charts

Weight line animates on range change (not on every recomposition).

Crosshair/marker interaction should be immediate and stable.

B3) Animation libraries/plugins to add (premium)

You already have the core Compose animation dependency. For “slick all the way,” add these:

Required (already listed / should be present)

androidx.compose.animation:animation

com.google.accompanist:accompanist-navigation-animation

Add for advanced motion (recommended)

androidx.constraintlayout:constraintlayout-compose (MotionLayout for premium transitions: card expand, header collapse, etc.)

androidx.compose.animation:animation-graphics (optional; animated vectors/graphics)

Optional premium polish

com.airbnb.android:lottie-compose (for high-quality micro-animations—use sparingly)

com.valentinilk.shimmer:compose-shimmer (for “Drafting…” skeletons—use sparingly)

B4) Haptics (part of premium feel)

No library needed; use Compose haptics APIs:

LocalHapticFeedback (light tap on confirm, selection, toggles; none on every keystroke)

Typography clarification (explicit)

Typography is included, and should be enforced via:

A single TrackyTypography object mapped into MaterialTheme.typography

A lint rule/convention: no raw Text(style=...) outside approved styles (or wrap with TrackyText)

Optional library (only if you want runtime Google Fonts):

androidx.compose.ui:ui-text-google-fonts

## 3.X Dark Mode (locked) — Tracky Dark Theme Specification

Dark mode is **supported and locked** to the token system. Dark mode must preserve Tracky’s “clean, sleek” look by emphasizing **subtle elevation, borders, and readable contrast**—not saturated neon accents or heavy shadows.

---

### 3.X.1 Usage distribution (screen-level, Dark)

- **Neutrals: 85%**
- **Brand Blue: 12%**
- **Accents: 3%**

Rationale: Dark UI becomes visually noisy quickly; reduce accent coverage to preserve premium minimalism.

---

### 3.X.2 Dark core palette (canonical tokens)

> No ad-hoc hex usage. Dark mode uses this token set only.

#### Neutrals (Dark)

- `Neutral/D0` `#0B0D11` (app background base)
- `Neutral/D50` `#0F1217` (background elevated zone)
- `Neutral/D100` `#121722` (surface / cards)
- `Neutral/D150` `#171D2A` (surface elevated / sheets)
- `Neutral/D200` `#1E2533` (subtle separators / input fill option)
- `Neutral/D300` `#2A3344` (borders/dividers)
- `Neutral/D500` `#98A2B3` (tertiary text)
- `Neutral/D700` `#C6CDD8` (secondary text)
- `Neutral/D900` `#F5F7FA` (primary text)

#### Brand (Dark)

- `Brand/Primary` `#2E6BB5` (same as light; used sparingly)
- `Brand/TintDark` `#0F2542` (tinted info surface for dark mode)
- `Brand/Deep` `#1E4D86` (pressed/active variants)

#### Semantic accents (Dark)

- `Success` `#34C759` (same as light; limited usage)
- `Warning` `#FF9500`
- `Error` `#FF3B30`

---

### 3.X.3 Theme mapping (Dark mode)

**Dark (optional toggle; system-following recommended)**

- Background: `Neutral/D0`
- Background elevated zone (top app bars / grouped sections): `Neutral/D50`
- Surface (cards / main containers): `Neutral/D100`
- Surface Elevated (bottom sheets / dialogs): `Neutral/D150`
- Primary text: `Neutral/D900`
- Secondary text: `Neutral/D700`
- Tertiary/metadata text: `Neutral/D500`
- Borders/dividers: `Neutral/D300` (1dp)
- Disabled: `Neutral/D500` at ~55–60% opacity
- Scrim (sheet dim): black @ ~45–55% (use Material defaults unless custom required)

---

### 3.X.4 Dark mode component rules (locked specs)

#### Buttons

**Primary**
- Fill: `Brand/Primary`
- Text: `Neutral/D900`
- Pressed: `Brand/Deep` or 8–12% black overlay
- Shadow: minimal; prefer none + crisp shape

**Secondary**
- Fill: transparent
- Border: 1dp `Brand/Primary` @ ~70%
- Text: `Brand/Primary`

**Tertiary**
- Text-only: `Brand/Primary`
- No underlines; rely on color + spacing

#### Inputs

- Height: 48
- Radius: 14
- Default fill: `Neutral/D100` (or `Neutral/D200` if you need extra separation)
- Border: 1dp `Neutral/D300`
- Focus border: `Brand/Primary`
- Placeholder text: `Neutral/D500`

#### Cards

**Base Card**
- Fill: `Neutral/D100`
- Border: 1dp `Neutral/D300`
- Avoid shadows unless necessary; if used, extremely subtle

**Tinted / Info Card**
- Fill: `Brand/TintDark`
- Text: `Neutral/D900`
- Accent text: `Brand/Primary` allowed (one accent only)

#### Chips (7-day strip, filters)

- Default: fill `Neutral/D100`, border `Neutral/D300`, text `Neutral/D700`
- Selected: fill `Brand/TintDark`, text `Brand/Primary`, border `Brand/Primary` @ ~55–70%
- Current day highlight (if distinct from selected): use border emphasis only (no extra colors)

#### Progress bars (kcal + macros)

- Track: `Neutral/D200`
- Fill: `Brand/Primary`
- Warning: `Warning`
- Always show numeric labels (no color-only encoding)

#### Bottom sheets (macro distribution, entry actions)

- Container fill: `Neutral/D150`
- Top radius: 24
- Handle: `Neutral/D300` @ ~70% opacity
- Dividers: 1dp `Neutral/D300`
- Scrim: black @ ~50% (or Material default)
- Sheet content transitions: fade + slight vertical slide (250–350ms)

#### Icons

- Default: `Neutral/D700`
- Selected: `Brand/Primary`
- Destructive: `Error`
- Use one icon style only (Material outlined recommended)

---

### 3.X.5 Dark mode motion + polish (required)

- Dark mode must not introduce new motion styles. Use the same motion rules as Light:
  - micro: 150–250ms
  - nav: 250–350ms
  - easing only (no linear except progress)
- Scrim fades must be smooth and consistent across sheets/dialogs.
- Avoid bright animated glows, neon pulses, or high-contrast shimmer. If shimmer is used, keep it subtle against dark surfaces.

---

### 3.X.6 Dark mode “do not” list (hard rules)

- No pure-black cards (`#000000`)—use `Neutral/D100` to preserve depth and reduce harsh contrast.
- No white borders on dark surfaces; borders must use `Neutral/D300`.
- No saturated gradients in UI chrome; gradients remain contextual only (hero moments).
- No multiple semantic colors in one component (e.g., blue + green + orange simultaneously).
- No high-glow shadows behind text or icons.

---

### 3.X.7 Implementation rules (Compose)

- Dark mode is controlled via Material3 color scheme using `isSystemInDarkTheme()` (system-following).
- Provide two token sets: `TrackyTokensLight` and `TrackyTokensDark`.
- All components must read colors from `TrackyTokens` only; never inline hex.
- Ensure status/navigation bars match theme:
  - Dark mode: status bar icons light; bar background near `Neutral/D0` or transparent depending on top layout

