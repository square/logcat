package logcat

import com.google.common.truth.Truth.assertThat
import logcat.LogPriority.INFO
import org.junit.After
import org.junit.Test

class LogcatTest {

  @After
  fun tearDown() {
    LogcatLogger.uninstall()
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
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    logcat { "Hi" }

    assertThat(logger.latestLog!!.message).isEqualTo("Hi")
  }

  @Test fun `logcat() captures tag from outer context class name`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    logcat { "Hi" }

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() tag overriding passes tag to logger`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    logcat(tag = "Bonjour") { "Hi" }

    assertThat(logger.latestLog!!.tag).isEqualTo("Bonjour")
  }

  @Test fun `logcat() passes priority to logger`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    logcat(INFO) { "Hi" }

    assertThat(logger.latestLog!!.priority).isEqualTo(INFO)
  }

  @Test fun `logcat() passes priority to isLoggable check`() {
    var receivedPriority: LogPriority? = null
    TestLogcatLogger(isLoggable = { receivedPriority = it; true })
      .apply { LogcatLogger.install(this); latestLog = null }

    logcat(INFO) { "Hi" }

    assertThat(receivedPriority).isEqualTo(INFO)
  }

  @Test fun `when not loggable, the message lambda isn't invoked`() {
    TestLogcatLogger(isLoggable = { false }).apply { LogcatLogger.install(this); latestLog = null }
    var count = 0

    logcat { "Yo${++count}" }

    assertThat(count).isEqualTo(0)
  }

  @Test fun `Throwable asLogMessage() has stacktrace logged`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }
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
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    standaloneFunctionLog(tag = "Bonjour", message = { "Hi" })

    with(logger.latestLog!!) {
      assertThat(tag).isEqualTo("Bonjour")
      assertThat(message).isEqualTo("Hi")
    }
  }

  @Test fun `logcat() captures outer this tag from lambda`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    val lambda = {
      logcat { "Hi" }
    }
    lambda()

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() captures outer this tag from nested lambda`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

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
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    val anonymousRunnable = object : Runnable {
      override fun run() {
        logcat { "Hi" }
      }
    }
    anonymousRunnable.run()

    assertThat(logger.latestLog!!.tag).isEqualTo(LogcatTest::class.java.simpleName)
  }

  @Test fun `logcat() captures tag from companion function`() {
    val logger = TestLogcatLogger().apply { LogcatLogger.install(this); latestLog = null }

    companionFunctionLog { "Hi" }

    assertThat(logger.latestLog!!.tag).isEqualTo(Companion::class.java.simpleName)
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
