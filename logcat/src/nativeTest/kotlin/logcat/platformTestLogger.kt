package logcat

actual fun platformTestLogger(isLoggable: (LogPriority) -> Boolean): ITestLogcatLogger {
  return NativeTestLogcatLogger(isLoggable = isLoggable)
}