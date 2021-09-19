package logcat

import android.util.Log

enum class LogPriority(
  val priorityInt: Int
) {
  VERBOSE(Log.VERBOSE),
  DEBUG(Log.DEBUG),
  INFO(Log.INFO),
  WARN(Log.WARN),
  ERROR(Log.ERROR),
  ASSERT(Log.ASSERT);
}