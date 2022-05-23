package logcat

fun Throwable.defaultThrowableAsLog(): String {
  printStackTrace()
  return stackTraceToString()
}
