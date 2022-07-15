package logcat

fun Throwable.defaultThrowableAsLog(): String {
  return stackTraceToString()
}
