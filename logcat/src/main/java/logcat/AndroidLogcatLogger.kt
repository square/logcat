package logcat

import android.util.Log
import logcat.LogPriority.DEBUG
import kotlin.math.min

private const val MAX_LOG_LENGTH = 4000

/**
 * A [logcat] logger that delegates to [android.util.Log] for any log with a priority of
 * at least [minPriorityInt], and is otherwise a no-op.
 *
 * Handles special cases for [LogPriority.ASSERT] (which requires sending to Log.wtf) and
 * splitting logs to be at most 4000 characters per line (otherwise logcat just truncates).
 *
 * The implementation is based on Timber DebugTree.
 */
class AndroidLogcatLogger(private val minPriorityInt: Int = DEBUG.priorityInt) : LogcatLogger {

  override fun isLoggable(priority: LogPriority): Boolean =
    priority.priorityInt >= minPriorityInt

  override fun log(
    priority: LogPriority,
    tag: String,
    message: String
  ) {
    if (message.length < MAX_LOG_LENGTH) {
      logToLogcat(priority.priorityInt, tag, message)
      return
    }

    // Split by line, then ensure each line can fit into Log's maximum length.
    var i = 0
    val length = message.length
    while (i < length) {
      var newline = message.indexOf('\n', i)
      newline = if (newline != -1) newline else length
      do {
        val end = min(newline, i + MAX_LOG_LENGTH)
        val part = message.substring(i, end)
        logToLogcat(priority.priorityInt, tag, part)
        i = end
      } while (i < newline)
      i++
    }
  }

  private fun logToLogcat(
    priority: Int,
    tag: String,
    part: String
  ) {
    if (priority == Log.ASSERT) {
      Log.wtf(tag, part)
    } else {
      Log.println(priority, tag, part)
    }
  }
}