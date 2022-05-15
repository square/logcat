package logcat

private fun String?.stripCompanions(): String? {
    return if (this.isNullOrEmpty() || this == "Companion") null else this
}

private fun fallbackToThrowableIck(): String {
    return Throwable().stackTraceToString().lines().dropWhile { !it.trimStart().startsWith("at Companion") }.drop(1).first().trimStart().removePrefix("at ").substringBefore(".")
}

actual fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
    val javascriptClass = this::class
    val fullClassName = javascriptClass.js.name
    val outerClassName = fullClassName.substringBefore('$')
    val simplerOuterClassName = outerClassName.substringAfterLast('.')
    return simplerOuterClassName.stripCompanions()
        ?: fullClassName.stripCompanions()
        ?: fallbackToThrowableIck()
}