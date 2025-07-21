package logcat.internal

@PublishedApi
internal expect fun <T> copyOnWriteArrayList(): MutableList<T>

@PublishedApi
internal expect fun <T> copyOnWriteArrayList(items: MutableList<T>): MutableList<T>
