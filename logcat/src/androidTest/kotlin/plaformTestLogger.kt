package logcat

actual fun platformTestLogger(isLoggable: (LogPriority) -> Boolean): ITestLogcatLogger {
  return TestLogcatLogger(isLoggable = isLoggable)
}
