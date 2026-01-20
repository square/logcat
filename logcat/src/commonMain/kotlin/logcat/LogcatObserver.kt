package logcat

/**
 * Called by [logcat]. [beforeLog] is called before evaluating the message lambda,
 * and [afterLog] is called after all [LogcatLogger.loggers] have received the message.
 * [afterLog] is guaranteed to be called if [beforeLog] was called, even if an exception
 * is thrown during logging or the observer is cleared right after [beforeLog]
 * is called. This makes it safe to use for tracing (e.g.,
 * [android.os.Trace.beginSection]/[android.os.Trace.endSection]).
 * Set implementation on [LogcatLogger.observer].
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
