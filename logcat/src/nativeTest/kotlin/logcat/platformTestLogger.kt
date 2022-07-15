package logcat

actual fun platformTestLogger(isLoggable: (LogPriority) -> Boolean): TestLogcatLogger {
  return NativeTestLogcatLogger(isLoggable = isLoggable)
}
