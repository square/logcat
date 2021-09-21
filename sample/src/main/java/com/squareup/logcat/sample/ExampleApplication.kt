package com.squareup.logcat.sample

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority.VERBOSE

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // Log all priorities in debug builds, no-op in release builds.
    AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = VERBOSE)
  }
}
