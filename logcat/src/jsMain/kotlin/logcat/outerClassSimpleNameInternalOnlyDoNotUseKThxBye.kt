package logcat

private fun String?.stripCompanions(): String? {
  return if (
    this.isNullOrEmpty() ||
    this.startsWith("Companion") ||
    this.startsWith("_no_name_provided")
  ) {
    null
  } else {
    this
  }
}

private fun fallbackToThrowableIck(): String {
  return Throwable().stackTraceToString()
    .lines()
    .asSequence()
    .map { it.trimStart() }
    .dropWhile { !it.startsWith("at outerClassSimpleNameInternalOnlyDoNotUseKThxBye") }
    .drop(1)
    .dropWhile {
      it.startsWith("at _no_name_provided") || it.startsWith("at Companion")
    }
    .first()
    .trimStart()
    .removePrefix("at ")
    .substringBefore(".")
}

@PublishedApi
internal actual fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
  val javascriptClass = this::class
  val fullClassName = javascriptClass.js.name
  val outerClassName = fullClassName.substringBefore('$')
  val simplerOuterClassName = outerClassName.substringAfterLast('.')
  return simplerOuterClassName.stripCompanions()
    ?: fullClassName.stripCompanions()
    ?: fallbackToThrowableIck()
}
