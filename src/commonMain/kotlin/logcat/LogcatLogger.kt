package logcat

/**
 * Logger that [logcat] delegates to. Call [install] to install a new logger, the default is a
 * no-op logger. Calling [uninstall] falls back to the default no-op logger.
 *
 * You should install [AndroidLogcatLogger] on Android and [PrintLogger] on a JVM/JS/Native.
 */
interface LogcatLogger {

  /**
   * Whether a log with the provided priority should be logged and the corresponding message
   * providing lambda evaluated. Called by [logcat].
   */
  fun isLoggable(priority: LogPriority) = true

  /**
   * Write a log to its destination. Called by [logcat].
   */
  fun log(
    priority: LogPriority,
    tag: String,
    message: String
  )

  companion object {
    // For expect functions below
  }
}

// region `expected` api for LogcatLogger.Companion
/**
 * The logger to use
 */
@PublishedApi
internal expect val LogcatLogger.Companion.logger: LogcatLogger

expect val LogcatLogger.Companion.isInstalled: Boolean

/**
 * Installs a [LogcatLogger].
 *
 * It is an error to call [install] more than once without calling [uninstall] in between,
 * however doing this won't throw, it'll log an error to the newly provided logger.
 */
expect fun LogcatLogger.Companion.install(logger: LogcatLogger)

/**
 * Replaces the current logger (if any) with a no-op logger.
 */
expect fun LogcatLogger.Companion.uninstall()
// endregion `expected` functions for LogcatLogger.Companion

/**
 * Implementation of NoLog - the default log to which no messages should be sent
 */
internal object NoLog : LogcatLogger {
  override fun isLoggable(priority: LogPriority) = false

  override fun log(
    priority: LogPriority,
    tag: String,
    message: String
  ) = error("Should never receive any log")
}
