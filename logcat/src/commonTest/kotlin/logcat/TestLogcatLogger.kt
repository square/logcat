package logcat

/**
 * Standard implementation of a LogcatLogger that can be used on Android/JVM/JS
 */
class TestLogcatLogger(private val isLoggable: (LogPriority) -> Boolean = { true }) :
  ITestLogcatLogger {
  override fun isLoggable(priority: LogPriority): Boolean = isLoggable.invoke(priority)

  override var latestLog: Log? = null

  override fun log(
    priority: LogPriority,
    tag: String,
    message: String
  ) {
    latestLog = Log(priority, tag, message)
  }
}
