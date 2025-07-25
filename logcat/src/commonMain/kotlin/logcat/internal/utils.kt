package logcat.internal

import logcat.InternalLogcatApi

@InternalLogcatApi
internal expect fun <T> threadSafeList(): MutableList<T>

@InternalLogcatApi
@PublishedApi
internal expect fun Any.outerClassSimpleName(): String
