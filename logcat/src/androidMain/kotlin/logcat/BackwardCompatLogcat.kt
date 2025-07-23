@file:JvmName("LogcatKt")

package logcat

import kotlin.jvm.JvmName

import logcat.internal.outerClassSimpleName

// Kept here for backward compat purposes
@Deprecated("This is an internal logcat API that should not be used from outside of logcat.")
@OptIn(InternalLogcatApi::class)
@Suppress("unused")
@PublishedApi
internal fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String = outerClassSimpleName()
