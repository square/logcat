@file:JvmName("LogcatKt")

package logcat

// Kept here for backward compat purposes
@PublishedApi
internal fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
  val javaClass = this::class.java
  val fullClassName = javaClass.name
  val outerClassName = fullClassName.substringBefore('$')
  val simplerOuterClassName = outerClassName.substringAfterLast('.')
  return if (simplerOuterClassName.isEmpty()) {
    fullClassName
  } else {
    simplerOuterClassName.removeSuffix("Kt")
  }
}
