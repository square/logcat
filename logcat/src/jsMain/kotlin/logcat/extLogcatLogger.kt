package logcat

private var privateLogger: LogcatLogger = NoLog
private var installedThrowable: Throwable? = null

@PublishedApi
internal actual val LogcatLogger.Companion.logger: LogcatLogger
  get() = privateLogger

actual fun LogcatLogger.Companion.install(logger: LogcatLogger) {
  if (isInstalled) {
    logger.log(
      LogPriority.ERROR,
      "LogcatLogger",
      "Installing $logger even though a logger was previously installed here: " +
        installedThrowable!!.asLog()
    )
  }
  installedThrowable = RuntimeException("Previous logger installed here")
  privateLogger = logger
}

actual fun LogcatLogger.Companion.uninstall() {
  privateLogger = NoLog
  installedThrowable = null
}

actual val LogcatLogger.Companion.isInstalled: Boolean
  get() = (privateLogger != NoLog)
