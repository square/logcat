package logcat

private fun String?.stripCompanions(): String? {
    return if (this.isNullOrEmpty() || this == "Companion") null else this
}

actual fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String {
    val nativeClass = this::class
    val fullClassName = nativeClass.simpleName.stripCompanions() ?: "$nativeClass"
    val outerClassName = fullClassName.removePrefix("class ").substringBefore('$')
    val simplerOuterClassName = outerClassName.removeSuffix(".Companion").substringAfterLast('.')

    println("nativeClass = $nativeClass")
    println("fullClassName = $fullClassName")
    println("outerClassName = $outerClassName")
    println("simpleOuterClassName = $simplerOuterClassName")
    return simplerOuterClassName.stripCompanions()
        ?: fullClassName.stripCompanions()
        ?: "Companion"
}
