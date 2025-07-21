package logcat.internal

import java.util.concurrent.CopyOnWriteArrayList

@PublishedApi
internal actual fun <T> copyOnWriteArrayList(items: MutableList<T>): MutableList<T> =
  CopyOnWriteArrayList(items)
