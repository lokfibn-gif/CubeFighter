# Cube Fighter

A fast-paced 3D Android fighting game built with LibGDX.

## Description

Cube Fighter is an action-packed mobile fighting game where players battle against waves of cube-shaped enemies. Features intuitive touch controls, dynamic combat mechanics, and progressive difficulty levels.

## Features

- 3D cube-based characters and enemies
- Touch-optimized controls for mobile gameplay
- Multiple enemy types with unique behaviors
- Progressive difficulty system
- Health and damage mechanics
- Score tracking system
- Responsive UI with LibGDX Scene2D
- Cross-platform support (Android/Desktop)

## Screenshots

> **Placeholder**: Add game screenshots here
> - Main menu screen
> - Gameplay action shot
> - Game over screen

## Installation

### Prerequisites

- Android device running Android 5.0 (API 21) or higher
- Approximately 50MB storage space

### Install from APK

1. Download the APK from the releases page
2. Enable "Install from unknown sources" in device settings
3. Open the APK file to install

## Development Setup

### Requirements

- **Java**: JDK 17 or higher
- **Gradle**: 8.x (wrapper included)
- **Android SDK**: API 34 (Android 14)
- **LibGDX**: 1.12.1
- IDE: IntelliJ IDEA or Android Studio (recommended)

### Environment Setup

1. Install JDK 17:
   ```bash
   # Ubuntu/Debian
   sudo apt install openjdk-17-jdk
   
   # macOS (via Homebrew)
   brew install openjdk@17
   
   # Windows (via Chocolatey)
   choco install openjdk17
   ```

2. Set JAVA_HOME environment variable:
   ```bash
   export JAVA_HOME=/path/to/jdk-17
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. Install Android SDK:
   - Download from https://developer.android.com/studio
   - Set ANDROID_HOME environment variable
   - Install required SDK packages via SDK Manager

4. Configure local.properties:
   ```bash
   cp local.properties.template local.properties
   # Edit and set your Android SDK path
   ```

## How to Build

### Desktop Build (For Testing)

```bash
./gradlew desktop:run
```

This runs the game on desktop for faster development iteration.

### Android Debug APK

```bash
./gradlew android:assembleDebug
```

Output: `android/build/outputs/apk/debug/android-debug.apk`

### Android Release APK

```bash
./gradlew android:assembleRelease
```

Output: `android/build/outputs/apk/release/android-release.apk`

### Clean Build

```bash
./gradlew clean build
```

## Project Structure

```
CubeFighter/
├── core/                   # Shared game logic
│   └── src/main/java/
│       └── com/cubefighter/
│           ├── CubeFighterGame.java    # Main game class
│           ├── screens/                # Game screens
│           ├── entities/               # Game entities
│           ├── systems/                # Game systems
│           └── utils/                  # Utility classes
├── desktop/                # Desktop launcher
│   └── src/main/java/
│       └── com/cubefighter/desktop/
│           └── DesktopLauncher.java
├── android/                # Android-specific code
│   ├── src/
│   │   └── com/cubefighter/android/
│   │       └── AndroidLauncher.java
│   ├── assets/             # Game assets (textures, sounds)
│   ├── res/                # Android resources
│   └── AndroidManifest.xml
├── build.gradle            # Root build configuration
├── settings.gradle         # Project settings
├── gradle.properties       # Gradle properties
└── local.properties        # Local SDK paths (gitignored)
```

## Controls

### Android (Touch)

| Action | Control |
|--------|---------|
| Move Left | Tap left side of screen |
| Move Right | Tap right side of screen |
| Attack | Tap attack button |
| Jump | Tap jump button |
| Special Move | Tap special button (when available) |

### Desktop (Testing)

| Action | Key |
|--------|-----|
| Move Left | A or Left Arrow |
| Move Right | D or Right Arrow |
| Jump | W or Space |
| Attack | J or Left Click |
| Special Move | K or Right Click |
| Pause | Escape |

## Game Mechanics

### Combat System

- **Basic Attack**: Quick melee attack dealing base damage
- **Special Attack**: Powerful attack with cooldown
- **Dodge**: Quick movement to avoid enemy attacks

### Health and Damage

- Player starts with 100 HP
- Enemies deal varying damage based on type
- Health pickups restore 25 HP

### Enemy Types

| Enemy | HP | Damage | Behavior |
|-------|-----|--------|----------|
| Basic Cube | 50 | 10 | Patrols and attacks on sight |
| Fast Cube | 30 | 5 | Quick movement, low damage |
| Heavy Cube | 150 | 25 | Slow, high damage |
| Boss Cube | 500 | 50 | Multi-phase attack patterns |

### Scoring

- Enemy defeated: Base points × difficulty multiplier
- Combo bonus: Additional points for consecutive hits
- Time bonus: Points for completing levels quickly

### Progression

- Levels increase in difficulty
- New enemy types introduced in later levels
- Boss battles every 5 levels

## Development

### Running Tests

```bash
./gradlew test
```

### Code Style

This project follows standard Java naming conventions:
- Classes: PascalCase
- Methods: camelCase
- Constants: UPPER_SNAKE_CASE
- Packages: lowercase

### Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

```
MIT License

Copyright (c) 2024 Cube Fighter

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Contact

For questions or issues, please open a GitHub issue.