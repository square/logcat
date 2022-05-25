package logcat

/**
 * Utility to turn a [Throwable] into a loggable string.
 */
actual fun Throwable.asLog(): String = defaultThrowableAsLog()
