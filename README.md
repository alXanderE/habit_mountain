# Habit Mountain

Habit Mountain is a Python/Kivy Android habit tracker. The first milestone establishes the game loop: add a habit, choose a cadence, complete it, earn coins, extend a streak, and see the climber progress up the mountain.

## Run locally on Windows

Install Python 3.11 or newer, then from this folder run:

```powershell
py -m venv .venv
.\\.venv\\Scripts\\Activate.ps1
python -m pip install --upgrade pip
pip install -r requirements.txt
python main.py
```

This opens a desktop preview of the mobile UI. The app can also run from Visual Studio Code or another Python editor.

## Export an Android APK on Windows

Buildozer uses the Android SDK/NDK toolchain and is most reliable on Linux. On Windows, use WSL2 with Ubuntu:

1. Install WSL2 and Ubuntu from PowerShell:

   ```powershell
   wsl --install -d Ubuntu
   ```

2. Inside Ubuntu, install the build dependencies:

   ```bash
   sudo apt update
   sudo apt install -y python3-pip python3-venv git zip unzip openjdk-17-jdk autoconf libtool pkg-config zlib1g-dev libncurses5-dev libncursesw5-dev libtinfo5 cmake libffi-dev libssl-dev
   ```

3. Keep the repository in the Linux filesystem for better build performance. From Ubuntu, clone it or copy it into your Linux home folder, then run:

   ```bash
   cd ~/habit_mountain
   python3 -m venv .venv
   source .venv/bin/activate
   pip install --upgrade pip buildozer cython==0.29.34
   buildozer -v android debug
   ```

4. The first build downloads the Android SDK/NDK and can take a while. When it finishes, the APK is placed in `bin/`, usually as `habitmountain-0.1-arm64-v8a_armeabi-v7a-debug.apk`.

5. Copy the APK back to Windows and install it on an Android device. You can either open the APK on the phone or use Android Debug Bridge:

   ```bash
   adb install -r bin/*.apk
   ```

For a release package, create a signing key, configure release signing in `buildozer.spec`, and run `buildozer android release`. Keep the keystore private and include the signed APK/AAB plus the source files and demo video in the final class zip.

## Project structure

- `main.py` contains the Kivy UI, mountain illustration, habit model, and local repository.
- `buildozer.spec` defines how the Python app becomes an Android package.
- `requirements.txt` defines the desktop Python dependency.
- `app/` and the Gradle files are the original Kotlin scaffold kept as a reference; Buildozer ignores them when packaging the Python app.

The current repository stores data in memory for fast iteration. The next structural step is persistence using JSON or SQLite, followed by notifications, character cosmetics, and a separate shop screen.
