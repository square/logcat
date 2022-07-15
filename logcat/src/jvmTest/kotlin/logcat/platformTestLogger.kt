package logcat

actual fun platformTestLogger(isLoggable: (LogPriority) -> Boolean): TestLogcatLogger {
  return CommonTestLogcatLogger(isLoggable = isLoggable)
}
