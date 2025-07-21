@file:JvmName("Logcat")

package logcat

import logcat.LogPriority.DEBUG
import logcat.internal.copyOnWriteArrayList
import kotlin.jvm.JvmName

/**
 * A tiny Kotlin API for cheap logging on top of Android's normal `Log` class.
 *
 * The [logcat] function has 3 parameters: an optional [priority], an optional [tag], and a required
 * string producing lambda ([message]). The lambda is only evaluated if a logger is installed and
 * the logger deems the priority loggable.
 *
 * The priority defaults to [LogPriority.DEBUG].
 *
 * The tag defaults to the class name of the log call site, without any extra runtime cost. This works
 * because [logcat] is an inlined extension function of [Any] and has access to [this] from which
 * it can extract the class name. If logging from a standalone function which has no [this], use the
 * [logcat] overload which requires a tag parameter.
 *
 * The [logcat] function does not take a [Throwable] parameter. Instead, the library provides
 * a Throwable extension function: [Throwable.asLog] which returns a loggable string.
 *
 * ```
 * import logcat.LogPriority.INFO
 * import logcat.asLog
 * import logcat.logcat
 *
 * class MouseController {
 *
 *   fun play {
 *     var state = "CHEEZBURGER"
 *     logcat { "I CAN HAZ $state?" }
 *     // logcat output: D/MouseController: I CAN HAZ CHEEZBURGER?
 *
 *     logcat(INFO) { "DID U ASK 4 MOAR INFO?" }
 *     // logcat output: I/MouseController: DID U ASK 4 MOAR INFO?
 *
 *     logcat { exception.asLog() }
 *     // logcat output: D/MouseController: java.lang.RuntimeException: FYLEZ KERUPTED
 *     //                        at sample.MouseController.play(MouseController.kt:22)
 *     //                        ...
 *
 *     logcat("Lolcat") { "OH HI" }
 *     // logcat output: D/Lolcat: OH HI
 *   }
 * }
 * ```
 *
 * To install a logger, see [LogcatLogger].
 *
 * @param tag If provided, the log will use this [tag] instead of the simple class name of [this] at
 * the call site.
 */
inline fun Any.logcat(
  priority: LogPriority = DEBUG,
  tag: String? = null,
  message: () -> String
) {
  if (!LogcatLogger.isInstalled) {
    return
  }
  val loggers = LogcatLogger.loggers.filter { it.isLoggable(priority) }
  if (loggers.isNotEmpty()) {
    val tagOrCaller = tag ?: outerClassSimpleNameInternalOnlyDoNotUseKThxBye()
    // Ensures beforeLog() and afterLog() are called on the same observers. Backing array reused.
    val observersSnapshot = copyOnWriteArrayList(LogcatLogger.observers)
    for (observer in observersSnapshot) {
      observer.beforeLog(priority, tagOrCaller)
    }
    val evaluatedMessage = message()
    for (logger in loggers) {
      logger.log(priority, tagOrCaller, evaluatedMessage)
    }
    for (observer in observersSnapshot) {
      observer.afterLog(priority, tagOrCaller)
    }
  }
}

/**
 * An overload for logging that does not capture the calling code as tag. This should only
 * be used in standalone functions where there is no `this`.
 * @see logcat above
 */
inline fun logcat(
  tag: String,
  priority: LogPriority = DEBUG,
  message: () -> String
) {
  if (!LogcatLogger.isInstalled) {
    return
  }
  val loggers = LogcatLogger.loggers.filter { it.isLoggable(priority) }
  if (loggers.isNotEmpty()) {
    // Ensures beforeLog() and afterLog() are called on the same observers. Backing array reused.
    val observersSnapshot = copyOnWriteArrayList(LogcatLogger.observers)
    for (observer in observersSnapshot) {
      observer.beforeLog(priority, tag)
    }
    val evaluatedMessage = message()
    for (logger in loggers) {
      logger.log(priority, tag, evaluatedMessage)
    }
    for (observer in observersSnapshot) {
      observer.afterLog(priority, tag)
    }
  }
}
