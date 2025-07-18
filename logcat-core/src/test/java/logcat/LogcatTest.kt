package logcat

import com.google.common.truth.Truth.assertThat
import logcat.LogPriority.INFO
import logcat.LogcatLogger.Companion.loggers
import org.junit.After
import org.junit.Test

class LogcatTest {

  private val logger = TestLogcatLogger().apply {
    loggers += this
  }

  @After
  fun tearDown() {
    LogcatLogger.uninstall()
    loggers -= logger
  }

  @Test fun `when no logger set, calling logcat() does not crash`() {
    logcat { "Yo" }
  }

  @Test fun `when no logger set, the message lambda isn't invoked`() {
    var count = 0

    logcat { "Yo${++count}" }

    assertThat(count).isEqualTo(0)
  }

  @Test fun `logcat() logs message from lambda`() {
    LogcatLogger.install()

    logcat { "Hi" }

    assertThat(logger.latestLog!!.message).isEqualTo("Hi")
  }

  @Test fun `logcat() captures tag from outer context class name`() {
    LogcatLogger.install()

    logcat { "Hi" }

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() tag overriding passes tag to logger`() {
    LogcatLogger.install()

    logcat(tag = "Bonjour") { "Hi" }

    assertThat(logger.latestLog!!.tag).isEqualTo("Bonjour")
  }

  @Test fun `logcat() passes priority to logger`() {
    LogcatLogger.install()

    logcat(INFO) { "Hi" }

    assertThat(logger.latestLog!!.priority).isEqualTo(INFO)
  }

  @Test fun `logcat() passes priority to isLoggable check`() {
    LogcatLogger.install()

    logcat(INFO) { "Hi" }

    assertThat(logger.latestPriority).isEqualTo(INFO)
  }

  @Test fun `when not loggable, the message lambda isn't invoked`() {
    logger.shouldLog = false
    var count = 0

    logcat { "Yo${++count}" }

    assertThat(count).isEqualTo(0)
  }

  @Test fun `Throwable asLogMessage() has stacktrace logged`() {
    LogcatLogger.install()
    val exception = RuntimeException("damn")

    logcat { exception.asLog() }

    assertThat(logger.latestLog!!.message).contains(
      """
      |java.lang.RuntimeException: damn
      |	at logcat.LogcatTest.Throwable asLogMessage() has stacktrace logged(LogcatTest.kt:
      """.trimMargin()
    )
  }

  @Test fun `standalone function can log with tag`() {
    LogcatLogger.install()

    standaloneFunctionLog(tag = "Bonjour", message = { "Hi" })

    with(logger.latestLog!!) {
      assertThat(tag).isEqualTo("Bonjour")
      assertThat(message).isEqualTo("Hi")
    }
  }

  @Test fun `logcat() captures outer this tag from lambda`() {
    LogcatLogger.install()

    val lambda = {
      logcat { "Hi" }
    }
    lambda()

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() captures outer this tag from nested lambda`() {
    LogcatLogger.install()

    val lambda = {
      val lambda = {
        logcat { "Hi" }
      }
      lambda()
    }
    lambda()

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() captures outer this tag from anonymous object`() {
    LogcatLogger.install()

    val anonymousRunnable = object : Runnable {
      override fun run() {
        logcat { "Hi" }
      }
    }
    anonymousRunnable.run()

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() captures tag from companion function`() {
    LogcatLogger.install()

    companionFunctionLog { "Hi" }

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  companion object {
    fun companionFunctionLog(
      message: () -> String
    ) {
      logcat(message = message)
    }
  }
}

fun standaloneFunctionLog(
  tag: String,
  message: () -> String
) {
  logcat(tag, message = message)
}
