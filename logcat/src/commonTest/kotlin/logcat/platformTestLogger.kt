package logcat

/**
 * On each platform, expect that the ITestLogcatLogger will be provided.  This
 * allows common tests, differing only in the TestLogcatLogger implementation
 * to be defined once, rather than for each platform
 */
expect fun platformTestLogger(isLoggable: (LogPriority) -> Boolean = { true }): ITestLogcatLogger
