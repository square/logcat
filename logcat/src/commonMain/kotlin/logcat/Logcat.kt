@file:JvmName("Logcat")

package logcat

import logcat.LogPriority.DEBUG
import logcat.internal.outerClassSimpleName

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
  @OptIn(InternalLogcatApi::class)
  val tagOrCaller = tag ?: outerClassSimpleName()
  val iterator = LogcatLogger.loggers.iterator()

  // Determine if there is anything to log
  val firstLogger = findFirstLogger(tagOrCaller, priority, iterator) ?: return

  // Evaluate the message to log
  val evaluatedMessage = message()

  // Do the logging
  sendToLoggers(tagOrCaller, priority, evaluatedMessage, firstLogger, iterator)
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
  Unit.logcat(priority, tag, message)
}

/**
 * This function is pulled out of logcat to save on code size. logcat is
 * inlined, but this one is not. This function is designed to answer the
 * question "do we need to call message()?"
 *
 * @param iterator All the LogcatLoggers
 * @return The first LogcatLogger for which isLoggable(priority, tag) returned
 * true, or null if there are none.
 */
@PublishedApi
@JvmName("internalDoNotCall_findFirstLogger")
internal fun findFirstLogger(
  tag: String,
  priority: LogPriority,
  iterator: Iterator<LogcatLogger>
): LogcatLogger? {
  while (iterator.hasNext()) {
    val logger = iterator.next()
    if (logger.isLoggable(priority, tag)) {
      return logger
    }
  }
  return null
}

/**
 * This function is pulled out of logcat to save on code size. logcat is
 * inlined, but this one is not. This function takes a concrete message as
 * input because passing the lambda to a non-inlined function would require
 * allocating an object for it.
 *
 * @param firstLogger The firstLogger for which isLoggable(priority, tag)
 * returned true
 * @param iterator All the LogcatLoggers after firstLogger
 */
@PublishedApi
@JvmName("internalDoNotCall_sendToLoggers")
internal fun sendToLoggers(
  tag: String,
  priority: LogPriority,
  message: String,
  firstLogger: LogcatLogger,
  remainingLoggers: Iterator<LogcatLogger>
) {
  val observer = LogcatLogger.observer
  observer?.beforeLog(priority, tag)

  // We want to ensure that beforeLog is paired with afterLog, even if
  // something within the body of the try throws. A simple try/finally has a
  // problem: If the code within finally (i.e. afterLog) throws, then it will
  // hide the original exception. To address this, we hold onto the original
  // exception and rethrow it.
  //
  // This could also be accomplished with a [java.io.Closeable.use], at the
  // cost of an extra allocation.
  var thrown: Throwable? = null
  try {
    firstLogger.log(priority, tag, message)
    while (remainingLoggers.hasNext()) {
      val logger = remainingLoggers.next()
      if (logger.isLoggable(priority, tag)) {
        logger.log(priority, tag, message)
      }
    }
  } catch (e: Throwable) {
    thrown = e
    throw e
  } finally {
    try {
      observer?.afterLog(priority, tag)
    } catch (afterLogException: Throwable) {
      thrown?.addSuppressed(afterLogException) ?: throw afterLogException
    }
  }
}
