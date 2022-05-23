package logcat

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

/**
 * LogcatLogger for use in native test cases that provides an AtomicReference and
 * object freezing to prevent InvalidMutabilityException when updating the latestLog
 */
class NativeTestLogcatLogger(private val isLoggable: (LogPriority) -> Boolean = { true }) :
  ITestLogcatLogger {
  override fun isLoggable(priority: LogPriority): Boolean = isLoggable.invoke(priority)

  private var _latestLog: AtomicReference<Log?> = AtomicReference(null)
  override var latestLog: Log?
    set(value) {
      _latestLog.value = value.freeze()
    }
    get() {
      return _latestLog.value
    }

  override fun log(
    priority: LogPriority,
    tag: String,
    message: String
  ) {
    latestLog = Log(priority, tag, message)
  }
}
