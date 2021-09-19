@file:Suppress("NOTHING_TO_INLINE")

package logcat

import logcat.LogPriority.DEBUG
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Kotlin utility for easy logging to logcat with cheap "magic tags" and lazy string evaluation.
 *
 * This should be preferred to Timber whenever logging from Kotlin code, as Timber makes it easy to
 * create performance issues with eager string interpolation for no-op logs.
 *
 * "Magic tags" in this context means using the simple class name of `this` at the call site as the tag
 * for the log.
 *
 * ```
 * class ThingOwner {
 *   fun doTheThing() {
 *     // This will be logged with the "ThingOwner" tag.
 *     logcat { "Did the thing" }
 *   }
 * }
 * ```
 *
 * Timber also supports "magic tags", but only with a significant performance tax.
 *
 * Logcat supports everything we could do with Timber, here are a few recipes:
 *
 * ```
 * // Log with format
 * Timber.d("State is %s at time %d", state, time)
 * logcat { "State is $state at time $time" }
 *
 * // Log with priority
 * Timber.i("an info message")
 * logcat(INFO) { "an info message" }
 *
 * // Log exception
 * Timber.d(exception)
 * logcat { exception.asLog() }
 *
 * // Log exception with message
 * Timber.d(exception, "state is %s", state)
 * logcat { exception.asLog("state is $state") }
 *
 * // Log with custom tag
 * Timber.tag("MyTag").d("hi")
 * logcat("MyTag") { "hi" }
 * ```
 *
 * Note: if logging from a standalone function that has no `this`, you'll need to use the
 * overload [logcat] that requires providing a tag.
 *
 * Ideas behind this approach:
 *
 * - Log statements should be easy to write and readable => logcat() is a function that can be called
 * from any kotlin code. The support for throwable is explicitly layered as an extension function to
 * keep the signature and usage clear.
 * - Default tags should be the calling class name, for cheap => logcat() is an extension function on
 * [Any] and uses that to capture `this` and get its class name.
 * - Log statements should be no-ops when logging is not enabled => the logcat() function is inlined
 * and the message is defined as a string producer lambda that is inlined as well and never runs if
 * when logging is off (no string interpolation!).
 * - Most developers just want to "send something to logcat" without ever thinking about priority
 * So, we pick "debug" as the "right thing" by default, they shouldn't have to learn that they should
 * call .d().
 * - Also we've over time started assuming "Timber.d = goes to logcat", which is not obvious to
 * newcomers. Hence the name `logcat()` to avoid any confusion as to what this method does.
 * - If you do care about priority and make a choice, then we should make that choice very obvious,
 * not just a one letter difference.
 * - Having N methods, one for each priority level, is not great. It increases the API surface, and
 * when navigating to that code developers now have N methods (M overloads) to read.
 * - Timber.e() is generally assumed to exists for exceptions, but not really ! After all you can pass
 * a throwable to all timber methods, it's only there as a priority level to make things more visible.
 * So more confusion and choices / overloads.
 * Timber also makes the assumptions that different runtimes will send logs to different places, and
 * also depending on priorities. E.g. you might have Timber.w go to bugsnag as a warning. `logcat()`
 * very intentionally do not support that. We want you to decide "hey this should be a breadcrumb"
 * (which are on a small ring buffer) vs "hey this should go to logcat" vs
 * "hey this should be a bugsnag warning". Hence the idea of the method being named "logcat"
 * - The lack of throwable parameter is also intentional. It just creates more overloads and confusion
 * (what's the param order??), when really logs are about strings and all you need is a super easy way
 * to turn a throwable into a string for logs.
 *
 */
inline fun Any.logcat(
  priority: LogPriority = DEBUG,
  /**
   * If provided, the log will use this tag instead of the simple class name of `this` at the call
   * site.
   */
  tag: String? = null,
  message: () -> String
) {
  LogcatLogger.logger.let { logger ->
    if (logger.isLoggable(priority)) {
      val tagOrCaller = tag ?: classSimpleName()
      logger.log(priority, tagOrCaller, message())
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
  with(LogcatLogger.logger) {
    if (isLoggable(priority)) {
      log(priority, tag, message())
    }
  }
}

fun Throwable.asLog(prefix: String = ""): String {
  val stringWriter = StringWriter(256)
  val printWriter = PrintWriter(stringWriter, false)
  if (prefix.isNotEmpty()) {
    printWriter.print(prefix)
    printWriter.print('\n')
  }
  printStackTrace(printWriter)
  printWriter.flush()
  return stringWriter.toString()
}

@PublishedApi
internal fun Any.classSimpleName(): String {
  val javaClass = this::class.java
  val simpleName = javaClass.simpleName
  if (simpleName.isNotEmpty()) {
    return simpleName
  }
  // Anonymous objects have empty string as simple class name.
  val fullClassName = javaClass.name
  val outerClassName = fullClassName.substringBefore('$')
  val simplerOuterClassName = outerClassName.substringAfterLast('.')
  return if (simplerOuterClassName.isEmpty()) {
    fullClassName
  } else {
    simplerOuterClassName.removeSuffix("Kt")
  }
}