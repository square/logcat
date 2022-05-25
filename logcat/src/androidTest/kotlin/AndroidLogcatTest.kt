package logcat

import logcat.LogPriority.INFO
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AndroidLogcatTest {

  @AfterTest
  fun tearDown() {
    LogcatLogger.uninstall()
  }

  @Test fun `logcat() passes priority to isLoggable check`() {
    var receivedPriority: LogPriority? = null
    TestLogcatLogger(isLoggable = { receivedPriority = it; true })
      .apply { LogcatLogger.install(this); latestLog = null }

    logcat(INFO) { "Hi" }

    assertEquals(INFO, receivedPriority)
  }

  @Test fun `Throwable asLogMessage() has stacktrace logged`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }
    val exception = RuntimeException("damn")

    logcat { exception.asLog() }

    val stackTraceStr = """
      |java.lang.RuntimeException: damn
      |	at logcat.AndroidLogcatTest.Throwable asLogMessage() has stacktrace logged(AndroidLogcatTest.kt:
      """.trimMargin()
    assertTrue(
      logger.latestLog!!.message.contains(stackTraceStr)
    )
  }
}