package com.jakewharton.sdkmanager.internal

import static com.android.SdkConstants.PLATFORM_DARWIN
import static com.android.SdkConstants.PLATFORM_LINUX
import static com.android.SdkConstants.PLATFORM_UNKNOWN
import static com.android.SdkConstants.PLATFORM_WINDOWS
import static com.android.SdkConstants.currentPlatform

enum SdkPlatform {
  WINDOWS('windows','zip'),
  LINUX('linux', 'tgz'),
  DARWIN('macosx', 'zip');

  static SdkPlatform get() {
    switch (currentPlatform()) {
      case PLATFORM_WINDOWS:
        return WINDOWS
      case PLATFORM_LINUX:
        return LINUX
      case PLATFORM_DARWIN:
        return DARWIN
      default:
        throw new IllegalStateException("Unknown platform.")
    }
  }

  final String suffix
  final String ext

  SdkPlatform(String suffix, String ext) {
    this.suffix = suffix
    this.ext = ext
  }
}

