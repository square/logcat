package logcat.internal

import logcat.InternalLogcatApi

@InternalLogcatApi
internal expect fun <T> copyOnWriteArrayList(): MutableList<T>

@InternalLogcatApi
@PublishedApi
internal expect fun <T> copyOnWriteArrayList(items: MutableList<T>): MutableList<T>

@InternalLogcatApi
@PublishedApi
internal expect fun Any.outerClassSimpleName(): String
