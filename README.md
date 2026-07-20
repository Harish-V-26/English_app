# English_app

An Android app (Jetpack Compose) built as a college English department project to help users
learn English vocabulary through categorized word lists, an animated flashcard-style carousel,
and multiple-choice practice quizzes.

## Features

- **Auth**: Email/password and Google Sign-In via Firebase Auth.
- **10 vocabulary categories**: 3 original categories plus 7 new categories built from the
  department's word documents — Advanced Vocabulary, Basic Vocabulary, Basic vs Advanced,
  Blended Words, Kitchen Vocabulary, Movement Vocabulary, and Vocab Twist (homographs).
- **Word carousel**: swipeable cards, text-to-speech pronunciation, 4 animation styles,
  difficulty rating, favorite, and save (bookmark).
- **Share**: share any word's definition and example via the Android share sheet.
- **Practice & Quiz**: multiple-choice quizzes generated per category, reachable from a
  shortcut on the Home screen or the "Practice" button inside a category.
- **Cloud-synced progress**: favorites, bookmarks, difficulty ratings, and quiz results are
  saved to Firestore per signed-in user, so nothing resets when you navigate away.
- **Dashboard**: shows real progress (words rated, quizzes taken, quiz accuracy, favorites)
  pulled from Firestore.

## Tech stack

- Kotlin 2.0.21, Jetpack Compose + Material3
- Firebase Auth + Firestore
- Navigation Compose, Coil, Android TextToSpeech

## Known gaps / next steps

- Facebook Login button exists in the UI but is not implemented.
- New category words use placeholder (gray box) images — real images can be added by
  setting each `Word`'s `imageUrl` and mapping it in `CarouselScreen.getImageResId()`.
- `google-services.json` still contains old/unused package-name client entries from earlier
  prototyping (`com.englishlearningapp`, `com.example.englishlearningapp`) alongside the
  active `com.example.english_app` entry — safe to clean up in the Firebase console.
- No automated tests beyond the default Android template tests.

## Setup

1. Open in Android Studio (Kotlin 2.0.21 / AGP 8.10.1 / compileSdk 35).
2. Make sure `app/google-services.json` is present (already included).
3. In the Firebase console, enable **Firestore Database** for this project if not already
   enabled, and set rules so each user can only read/write their own `users/{uid}/...` data,
   e.g.:

   ```
   match /users/{userId}/{document=**} {
     allow read, write: if request.auth != null && request.auth.uid == userId;
   }
   ```

4. Run on an emulator or device.
