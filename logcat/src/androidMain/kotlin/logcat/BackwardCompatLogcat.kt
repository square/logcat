@file:JvmName("LogcatKt")

package logcat

import kotlin.jvm.JvmName

import logcat.internal.outerClassSimpleName

// Kept here for backward compat purposes
@Suppress("unused")
@PublishedApi
internal fun Any.outerClassSimpleNameInternalOnlyDoNotUseKThxBye(): String = outerClassSimpleName()
