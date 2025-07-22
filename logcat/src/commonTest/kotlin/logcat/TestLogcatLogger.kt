package logcat

import kotlin.test.assertEquals

class TestLogcatLogger : LogcatLogger {
  override fun isLoggable(priority: LogPriority): Boolean {
    latestPriority = priority
    return shouldLog
  }

  data class Log(
    val priority: LogPriority,
    val tag: String,
    val message: String
  )

  var latestLog: Log? = null
  var latestPriority: LogPriority? = null
  var shouldLog = true

  override fun log(
    priority: LogPriority,
    tag: String,
    message: String
  ) {
    latestLog = Log(priority, tag, message)
  }

  fun assertLatest(
    priority: LogPriority,
    tag: String,
    message: String
  ) = assertEquals(
    expected = Log(priority, tag, message),
    actual = latestLog
  )
}
