# Latest Changes

## Food Entry Swipe-to-Delete
- Extracted `SwipeableRow` to `c:\Tracky\app\src\main\java\com\tracky\app\ui\components\SwipeableRow.kt`.
- Updated `EntryDetailScreen` to use `SwipeableRow` for food items.
- Added `deleteFoodItem` to `EntryDetailViewModel`.

## Dynamic Entry Title
- Updated `EntryDetailScreen` top bar to show `First Item Name + X others` instead of static "Food Entry".
- Added `AnnotatedString` support to `TrackyTopBar`.

## Onboarding BMI Color
- Updated `OnboardingScreen.kt` to tint the BMI card background based on BMI classification (Green/Yellow/Red).
- Updated `TrackyMainCard` to accept `containerColor` and `borderColor`.
