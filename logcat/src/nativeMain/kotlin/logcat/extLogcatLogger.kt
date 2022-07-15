package logcat

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

private val loggerReference: AtomicReference<LogcatLogger> = AtomicReference(NoLog)
private val installedThrowable: AtomicReference<Throwable?> = AtomicReference(null)

@PublishedApi
internal actual val LogcatLogger.Companion.logger: LogcatLogger
  get() = loggerReference.value

actual fun LogcatLogger.Companion.install(logger: LogcatLogger) {
  logger.freeze()
  loggerReference.compareAndSet(NoLog, logger).also { updated ->
    if (!updated) {
      logger.log(
        LogPriority.ERROR,
        "LogcatLogger",
        "Installing $logger even though a logger was previously installed here: " +
          installedThrowable.value?.asLog()
      )
      // Set it anyway
      loggerReference.value = logger
    }
    installedThrowable.value = RuntimeException("Previous logger installed here").freeze()
  }
}

actual fun LogcatLogger.Companion.uninstall() {
  loggerReference.value = NoLog
  installedThrowable.value = null
}

actual val LogcatLogger.Companion.isInstalled: Boolean
  get() = (loggerReference.value != NoLog)
