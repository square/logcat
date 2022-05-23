package logcat

import logcat.LogPriority.INFO
import kotlin.native.concurrent.AtomicReference
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NativeLogcatTest {

  @AfterTest
  fun tearDown() {
    LogcatLogger.uninstall()
  }

  @Test fun logcat_passes_priority_to_isLoggable_check() {
    val receivedPriority: AtomicReference<LogPriority?> = AtomicReference(null)
    NativeTestLogcatLogger(isLoggable = { receivedPriority.value = it; true })
      .apply { LogcatLogger.install(this); latestLog = null }

    logcat(INFO) { "Hi" }

    assertEquals(INFO, receivedPriority.value)
  }

  @Test fun Throwable_asLogMessage_has_stacktrace_logged() {
    val logger = NativeTestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }
    val exception = RuntimeException("damn")

    logcat { exception.asLog() }

    val stackTraceStr = """
      |kotlin.RuntimeException: damn
      |    at 0
      """.trimMargin()
    assertTrue(
      logger.latestLog!!.message.contains(stackTraceStr)
    )
  }

}
