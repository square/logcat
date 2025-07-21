package logcat

/**
 * Called by [logcat]. [beforeLog] is called before evaluating the message lambda,
 * and [afterLog] is called after all [LogcatLogger.loggers] have received the message.
 * Add implementations to [LogcatLogger.observers].
 */
interface LogcatObserver {

  fun beforeLog(
    priority: LogPriority,
    tag: String
  )

  fun afterLog(
    priority: LogPriority,
    tag: String
  )
}
