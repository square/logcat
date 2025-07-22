@file:JvmName("LogcatKt")

package logcat

import kotlin.jvm.JvmName

@PublishedApi
internal actual fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
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
