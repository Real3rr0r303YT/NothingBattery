# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/).

## [0.1.0] - 2026-10-30

### Added
- Live battery monitor via the native `BatteryManager`: level, charging status and
  power source, health, temperature (°C), voltage (mV), current (mA), power (W),
  charge counter (mAh), estimated capacity, cycle count (Android 14+) and technology.
- Charge-limit warning at 80 % or 85 %: on-screen indicator (approaching / reached) and an
  optional notification from a lightweight offline foreground service. Re-arms after
  unplugging; restored after reboot.
- Nothing-OS-inspired dashboard: pure black, white and red, hand-drawn 5×7 dot-matrix
  digits, dot ring gauge and a 60-second dot-matrix current graph.
- About section with GPL-3.0 notice, offline full-text license viewer, *Buy me a coffee* and *Source code* links (opened in
  the external browser; the app still has no INTERNET permission).
- English and German translations.
- GitHub Actions: CI (unit tests, lint, debug APK, offline-permission check) and tag-driven
  signed release builds.
- Licensed under GPL-3.0 with SPDX headers in all source files. Made by Real3rr0r303YT.
