#!/usr/bin/env bash
# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.
# Fails if an APK requests any network-related permission. NothingBattery must stay 100 % offline.
set -euo pipefail

apk="${1:?usage: check-permissions.sh <apk>}"
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
aapt2="$(ls -d "$sdk"/build-tools/*/ | sort -V | tail -1)aapt2"

echo "Permissions requested by $(basename "$apk"):"
perms="$("$aapt2" dump permissions "$apk" | sed -n "s/^uses-permission: name='\([^']*\)'.*/\1/p")"
echo "$perms" | sed 's/^/  - /'

forbidden='^android\.permission\.(INTERNET|ACCESS_NETWORK_STATE|ACCESS_WIFI_STATE|CHANGE_NETWORK_STATE|CHANGE_WIFI_STATE)$'
if echo "$perms" | grep -Eq "$forbidden"; then
  echo "::error::Network permission found in $apk — NothingBattery must not access the network."
  exit 1
fi
echo "OK: no network permissions."
