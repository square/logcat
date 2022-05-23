package logcat

actual fun platformTestLogger(isLoggable: (LogPriority) -> Boolean): ITestLogcatLogger = NativeTestLogcatLogger(isLoggable = isLoggable)
