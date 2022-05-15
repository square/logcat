package logcat

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

class NativeTestLogcatLogger(private val isLoggable: (LogPriority) -> Boolean = { true }) :
  LogcatLogger {
  override fun isLoggable(priority: LogPriority): Boolean = isLoggable.invoke(priority)

  data class Log(
    val priority: LogPriority,
    val tag: String,
    val message: String
  )

  private var _latestLog: AtomicReference<Log?> = AtomicReference(null)
  var latestLog: Log?
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
