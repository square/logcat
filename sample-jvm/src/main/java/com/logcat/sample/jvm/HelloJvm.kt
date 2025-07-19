package com.logcat.sample.jvm

import logcat.logcat

object HelloJvm {
  fun logHello() {
    logcat { "Hello from JVM module!" }
  }
}
