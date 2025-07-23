package logcat.internal

import logcat.InternalLogcatApi
import java.util.concurrent.CopyOnWriteArrayList

@InternalLogcatApi
internal actual fun <T> copyOnWriteArrayList(): MutableList<T> = CopyOnWriteArrayList()

@InternalLogcatApi
@PublishedApi
internal actual fun <T> copyOnWriteArrayList(items: MutableList<T>): MutableList<T> =
  CopyOnWriteArrayList(items)

@InternalLogcatApi
@PublishedApi
internal actual fun Any.outerClassSimpleName(): String {
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
