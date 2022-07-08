package logcat

@PublishedApi
internal fun Any.defaultOuterClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
  val javaClass = this::class
  val fullClassName = javaClass.simpleName
  val outerClassName = fullClassName?.substringBefore('$')
  val simplerOuterClassName = outerClassName?.substringAfterLast('.')
  return if (simplerOuterClassName?.isEmpty() == true) {
    fullClassName
  } else {
    simplerOuterClassName?.removeSuffix("Kt") ?: "${this::class}"
  }
}
