[app]
title = Habit Mountain
package.name = habitmountain
package.domain = org.habitmountain
source.dir = .
source.include_exts = py,png,jpg,kv,atlas,json
source.exclude_dirs = .git,app,.idea,.gradle,bin,.buildozer
version = 0.1
requirements = python3,kivy==2.3.2
orientation = portrait
fullscreen = 0
android.api = 35
android.minapi = 26
android.archs = arm64-v8a, armeabi-v7a
android.accept_sdk_license = True

[buildozer]
log_level = 2
warn_on_root = 1
