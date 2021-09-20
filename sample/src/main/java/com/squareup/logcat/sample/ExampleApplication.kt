package com.squareup.logcat.sample

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // Log in debug builds, no-op in release builds.
    if (BuildConfig.DEBUG) {
      LogcatLogger.install(AndroidLogcatLogger())
    }
  }
}