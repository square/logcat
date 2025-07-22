package logcat.internal

import java.util.concurrent.CopyOnWriteArrayList

internal actual fun <T> copyOnWriteArrayList(): MutableList<T> = CopyOnWriteArrayList()

@PublishedApi
internal actual fun <T> copyOnWriteArrayList(items: MutableList<T>): MutableList<T> =
  CopyOnWriteArrayList(items)
