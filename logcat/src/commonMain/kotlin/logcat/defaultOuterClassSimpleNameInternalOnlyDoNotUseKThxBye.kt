package logcat

internal fun Any.defaultOuterClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
  val javaClass = this::class
  val fullClassName = javaClass.simpleName
  val outerClassName = fullClassName?.substringBefore('$')
  val simplerOuterClassName = outerClassName?.substringAfterLast('.')
  println("javaClass = $javaClass")
  println("fullClassName = $fullClassName")
  println("outerClassName = $outerClassName")
  println("simpleOuterClassName = $simplerOuterClassName")
  return if (simplerOuterClassName?.isEmpty() == true) {
    fullClassName
  } else {
    simplerOuterClassName?.removeSuffix("Kt") ?: "${this::class}"
  }
}
