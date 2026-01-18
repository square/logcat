package logcat

class TestLogcatLogger : LogcatLogger {
  override fun isLoggable(
    priority: LogPriority,
    tag: String
  ): Boolean {
    latestPriority = priority
    return shouldLog
  }

  data class Log(
    val priority: LogPriority,
    val tag: String,
    val message: String
  )

  var latestLog: Log? = null
  val allLogs = mutableListOf<Log>()
  var latestPriority: LogPriority? = null
  var shouldLog = true

  override fun log(
    priority: LogPriority,
    tag: String,
    message: String
  ) {
    val log = Log(priority, tag, message)
    latestLog = log
    allLogs += log
  }
}
