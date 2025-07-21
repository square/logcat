@file:JvmName("ThrowablesKt")

package logcat

import kotlin.jvm.JvmName

actual fun Throwable.asLog() = customJvmStackTraceToString()
