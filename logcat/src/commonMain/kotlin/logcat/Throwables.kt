package logcat

/**
 * Converts a [Throwable] into a loggable string representation.
 *
 * On non-JVM platforms, this function delegates to [kotlin.stackTraceToString].
 * On the JVM, it mimics the behavior of [kotlin.stackTraceToString] but uses a larger
 * buffer (256 bytes instead of the default 16) in the internal `StringWriter` for better performance.
 */
expect fun Throwable.asLog(): String
