package logcat

import logcat.LogcatLogger.Companion.install
import logcat.LogcatLogger.Companion.uninstall
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor

/**
 * Logger that [logcat] delegates to. Call [install] to install a new logger, the default is a
 * no-op logger. Calling [uninstall] falls back to the default no-op logger.
 *
 * You should install [AndroidLogcatLogger] on Android and [PrintLogger] on a JVM.
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
    @JvmStatic
    val loggers: MutableList<LogcatLogger> = CopyOnWriteArrayList()

    @Volatile
    @PublishedApi
    internal var logExecutor: Executor? = null
      private set

    @Volatile
    private var installedThrowable: Throwable? = null

    private val installLock = Any()

    @JvmStatic
    val isInstalled: Boolean
      get() = installedThrowable != null

    /**
     * Installs the Logcat library, enabling logging. Logs will not actually be evaluated
     * until at least one logger is added to [loggers].
     *
     * Pass in an optional [logExecutor] to evaluate log messages on a different thread.
     *
     * Libraries should check [isInstalled] before calling this, to avoid overriding any app
     * set [logExecutor].
     *
     * It is an error to call [install] more than once without calling [uninstall] in between,
     * however doing this won't throw, it'll log an error to the newly provided logger.
     */
    @JvmStatic
    fun install(logExecutor: Executor = Executor { it.run() }) {
      synchronized(installLock) {
        if (isInstalled) {
          println(
            "Installing LogcatLogger even though it was previously installed here: " +
              installedThrowable!!.asLog()
          )
        }
        installedThrowable = RuntimeException("LogcatLogger previously installed here")
        Companion.logExecutor = logExecutor
      }
    }

    /**
     * Disables logging.
     */
    @JvmStatic
    fun uninstall() {
      synchronized(installLock) {
        installedThrowable = null
        logExecutor = null
      }
    }

    @Deprecated(
      "Maintains backward binary compat for libraries that depend on v0.1 with " +
        "inline logcat {} calls"
    )
    @PublishedApi
    internal val logger: LogcatLogger
      get() {
        // New instance per call so that "local" fields aren't retained.
        return object : LogcatLogger {

          private lateinit var localLoggers: List<LogcatLogger>
          private var localLogExecutor: Executor? = null

          override fun isLoggable(priority: LogPriority): Boolean {
            localLogExecutor = logExecutor ?: return false
            val filteredLoggers = loggers.filter { it.isLoggable(priority) }
            localLoggers = filteredLoggers
            return filteredLoggers.isNotEmpty()
          }

          override fun log(
            priority: LogPriority,
            tag: String,
            message: String
          ) {
            localLogExecutor!!.execute {
              for (logger in localLoggers) {
                logger.log(priority, tag, message)
              }
            }
          }
        }
      }

    @Deprecated(
      "LogcatLogger.install() does not take a LogcatLogger instance anymore",
      ReplaceWith("install()")
    )
    fun install(logger: LogcatLogger) {
      install()
      loggers += logger
    }
  }
}
