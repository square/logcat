package logcat

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import logcat.LogcatLogger.Companion.install
import logcat.LogcatLogger.Companion.uninstall
import logcat.internal.threadSafeList
import kotlin.concurrent.Volatile

/**
 * Logger that [logcat] delegates to. Call [install] to enable logging, then add a [LogcatLogger]
 * instance to [LogcatLogger.loggers].
 *
 * You should install [AndroidLogcatLogger] on Android and [PrintLogger] on a JVM.
 *
 * You can get a good default by using [AndroidLogcatLogger.installOnDebuggableApp]
 */
interface LogcatLogger {

  @Deprecated(
    "Override the method that takes priority and tag instead",
    ReplaceWith("isLoggable(priority, tag)")
  )
  fun isLoggable(priority: LogPriority) = true

  /**
   * Whether a log with the provided priority and tag should be logged and the corresponding message
   * providing lambda evaluated. Called by [logcat].
   */
  fun isLoggable(
    priority: LogPriority,
    tag: String
  ) = isLoggable(priority)

  /**
   * Write a log to its destination. Called by [logcat].
   */
  fun log(
    priority: LogPriority,
    tag: String,
    message: String
  )

  companion object {
    /** @see LogcatLogger */
    @JvmStatic
    val loggers: MutableList<LogcatLogger> =
      @OptIn(InternalLogcatApi::class)
      threadSafeList()

    /** @see LogcatObserver */
    @JvmStatic
    @Volatile
    var observer: LogcatObserver? = null

    @Volatile
    private var installedThrowable: Throwable? = null

    private val installLock = SynchronizedObject()

    @JvmStatic
    val isInstalled: Boolean
      get() = installedThrowable != null

    /**
     * Installs the Logcat library, enabling logging. Logs will not actually be evaluated
     * until at least one logger is added to [loggers].
     *
     * It is an error to call [install] more than once without calling [uninstall] in between,
     * however doing this won't throw, it'll log an error to the newly provided logger.
     */
    @JvmStatic
    fun install() {
      synchronized(installLock) {
        if (isInstalled) {
          println(
            "Installing LogcatLogger even though it was previously installed here: " +
              installedThrowable!!.asLog()
          )
        }
        installedThrowable = RuntimeException("LogcatLogger previously installed here")
      }
    }

    /**
     * Disables logging.
     */
    @JvmStatic
    fun uninstall() {
      synchronized(installLock) {
        installedThrowable = null
      }
    }

    @Deprecated(
      "Maintains backward binary compat for libraries that depend on v0.1 with " +
        "inline logcat {} calls"
    )
    @PublishedApi
    @InternalLogcatApi
    internal val logger: LogcatLogger
      get() {
        // New instance per call so that "local" fields aren't retained.
        return object : LogcatLogger {

          private lateinit var localLoggers: List<LogcatLogger>

          override fun isLoggable(
            priority: LogPriority,
            tag: String
          ): Boolean {
            if (!isInstalled) {
              return false
            }
            val filteredLoggers = loggers.filter { it.isLoggable(priority, tag) }
            localLoggers = filteredLoggers
            return filteredLoggers.isNotEmpty()
          }

          override fun log(
            priority: LogPriority,
            tag: String,
            message: String
          ) {
            for (logger in localLoggers) {
              logger.log(priority, tag, message)
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
