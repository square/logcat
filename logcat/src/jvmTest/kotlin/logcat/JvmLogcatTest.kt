package logcat

import logcat.LogPriority.INFO
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmLogcatTest {

  @AfterTest
  fun tearDown() {
    LogcatLogger.uninstall()
  }

  @Test fun `logcat() passes priority to isLoggable check`() {
    var receivedPriority: LogPriority? = null
    platformTestLogger(isLoggable = { receivedPriority = it; true })
      .apply { LogcatLogger.install(this); latestLog = null }

    logcat(INFO) { "Hi" }

    assertEquals(INFO, receivedPriority)
  }

  @Test fun `Throwable asLogMessage() has stacktrace logged`() {
    val logger = platformTestLogger().apply { LogcatLogger.install(this); latestLog = null }
    val exception = RuntimeException("damn")

    logcat { exception.asLog() }

    val stackTraceStr = """
      |java.lang.RuntimeException: damn
      |	at logcat.JvmLogcatTest.Throwable asLogMessage() has stacktrace logged(JvmLogcatTest.kt:
      """.trimMargin()
    assertTrue(
      logger.latestLog!!.message.contains(stackTraceStr)
    )
  }
}
