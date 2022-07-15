package logcat

import logcat.LogPriority.INFO
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsLogcatTest {

  @AfterTest
  fun tearDown() {
    LogcatLogger.uninstall()
  }

  @Test fun logcat_passes_priority_to_isLoggable_check() {
    var receivedPriority: LogPriority? = null
    platformTestLogger(isLoggable = { receivedPriority = it; true })
      .apply { LogcatLogger.install(this); latestLog = null }

    logcat(INFO) { "Hi" }

    assertEquals(INFO, receivedPriority)
  }

  @Test fun Throwable_asLogMessage_has_stacktrace_logged() {
    val logger = platformTestLogger().apply { LogcatLogger.install(this); latestLog = null }
    val exception = RuntimeException("damn")

    logcat { exception.asLog() }

    val stackTraceStr = """
      |RuntimeException: damn
      |    at JsLogcatTest.Throwable_asLogMessage_has_stacktrace_logged
      """.trimMargin()
    assertTrue(
      logger.latestLog!!.message.contains(stackTraceStr)
    )
  }
}
