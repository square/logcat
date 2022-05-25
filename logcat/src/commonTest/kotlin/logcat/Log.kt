package logcat

data class Log(
  val priority: LogPriority,
  val tag: String,
  val message: String
)
