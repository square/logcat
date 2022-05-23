package logcat

actual fun platformTestLogger(isLoggable: (LogPriority) -> Boolean): ITestLogcatLogger = TestLogcatLogger(isLoggable = isLoggable)
