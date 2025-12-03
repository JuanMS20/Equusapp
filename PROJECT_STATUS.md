# Project Status

## Project Context
**EquusApp** (CaballoApp) is an interactive, free, and scientifically grounded mobile application designed as a pedagogical tool for studying the myology and functional biomechanics of the Colombian Criollo Horse. It is targeted at veterinary medicine students and professionals.

## Tech Stack
- **Platform:** Android (Min API 24, Target 35)
- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **DI:** Hilt
- **UI:** XML with ViewBinding / Material Design 3
- **Data:** Local storage (Hardcoded data / Room - to be verified)
- **Images:** PhotoView for interactive zooming/panning

## Architecture
The project follows a clean MVVM architecture:
- `ui/`: ViewModels and Activities/Fragments
- `data/`: Repositories and Data Sources
- `di/`: Dependency Injection modules
- `domain/`: (Implicit in Repositories/UseCases if present)

## Current Features (Based on PRD)
- **Interactive Anatomic Viewer:** Zoom and pan with clickable hotspots.
- **Regional Navigation:** Head, Neck, Trunk, Thoracic Limbs, Pelvic Limbs.
- **Detailed Muscle Info:** Origin, insertion, biomechanical function.
- **Quiz System:** 70+ questions.
- **Gamification:**
    - **XP & Levels:** Progression system based on quiz performance.
    - **Achievements:** Unlockable badges for milestones.
    - **Streaks:** Daily study tracking.
    - **UI:** Stats dashboard in Main Menu, Achievement popups in Quiz, Dedicated Achievements Screen.
- **Accessibility:** 5 colorblindness modes, adjustable text size.
- **Offline Functionality:** 100% local operation.

## Pending / Roadmap
- **iOS Version:** Future phase.
- **Additional Content:** Nervous, Circulatory systems.
- **AR:** Augmented Reality features.
