package logcat

import logcat.LogcatLogger.Companion

private object LogcatLoggerExt {
    @Volatile
    @PublishedApi
    internal var logger: LogcatLogger = NoLog
        private set

    @Volatile
    private var installedThrowable: Throwable? = null

    val isInstalled: Boolean
        get() = installedThrowable != null

    /**
     * Installs a [LogcatLogger].
     *
     * It is an error to call [install] more than once without calling [uninstall] in between,
     * however doing this won't throw, it'll log an error to the newly provided logger.
     */
    fun install(logger: LogcatLogger) {
        synchronized(this) {
            if (isInstalled) {
                logger.log(
                    LogPriority.ERROR,
                    "LogcatLogger",
                    "Installing $logger even though a logger was previously installed here: " +
                            installedThrowable!!.asLog()
                )
            }
            installedThrowable = RuntimeException("Previous logger installed here")
            LogcatLoggerExt.logger = logger
        }
    }

    /**
     * Replaces the current logger (if any) with a no-op logger.
     */
    fun uninstall() {
        synchronized(this) {
            installedThrowable = null
            logger = NoLog
        }
    }
}

@PublishedApi
internal actual val LogcatLogger.Companion.logger: LogcatLogger
    get() = LogcatLoggerExt.logger

actual fun LogcatLogger.Companion.install(logger: LogcatLogger) = LogcatLoggerExt.install(logger)

actual fun LogcatLogger.Companion.uninstall() = LogcatLoggerExt.uninstall()
actual val Companion.isInstalled: Boolean
  get() = LogcatLoggerExt.isInstalled
