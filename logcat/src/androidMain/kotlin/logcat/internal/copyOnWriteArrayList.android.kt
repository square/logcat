package logcat.internal

import java.util.concurrent.CopyOnWriteArrayList

internal actual fun <T> copyOnWriteArrayList(): MutableList<T> = CopyOnWriteArrayList()
