package logcat

import com.varabyte.truthish.assertThat
import logcat.LogPriority.INFO
import logcat.LogcatLogger.Companion.loggers
import logcat.LogcatLogger.Companion.observer
import kotlin.test.AfterTest
import kotlin.test.Test

class LogcatTest {

  private val logger = TestLogcatLogger().apply {
    loggers += this
  }

  @AfterTest
  fun tearDown() {
    LogcatLogger.uninstall()
    loggers.clear()
    observer = null
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

  @Test fun `afterLog is called even when later logger throws`() {
    LogcatLogger.install()
    val loggerException = RuntimeException("logger boom")
    val throwingLogger = object : LogcatLogger {
      override fun log(priority: LogPriority, tag: String, message: String) {
        throw loggerException
      }
    }
    loggers += throwingLogger
    var afterLogCalled = false
    LogcatLogger.observer = object : LogcatObserver {
      override fun beforeLog(priority: LogPriority, tag: String) {}
      override fun afterLog(priority: LogPriority, tag: String) {
        afterLogCalled = true
      }
    }

    val thrown = runCatching { logcat { "Hi" } }.exceptionOrNull()

    assertThat(logger.latestLog!!.message).isEqualTo("Hi")
    assertThat(afterLogCalled).isTrue()
    assertThat(thrown).isSameAs(loggerException)
  }

  @Test fun `afterLog is called even when first logger throws`() {
    LogcatLogger.install()
    loggers.clear()
    val loggerException = RuntimeException("first logger boom")
    val throwingLogger = object : LogcatLogger {
      override fun log(priority: LogPriority, tag: String, message: String) {
        throw loggerException
      }
    }
    loggers += throwingLogger
    var afterLogCalled = false
    observer = object : LogcatObserver {
      override fun beforeLog(priority: LogPriority, tag: String) {}
      override fun afterLog(priority: LogPriority, tag: String) {
        afterLogCalled = true
      }
    }

    val thrown = runCatching { logcat { "Hi" } }.exceptionOrNull()

    assertThat(afterLogCalled).isTrue()
    assertThat(thrown).isSameAs(loggerException)
  }

  @Test fun `logger exception propagates when both logger and afterLog throw`() {
    LogcatLogger.install()
    loggers.clear()
    val loggerException = RuntimeException("logger boom")
    val afterLogException = RuntimeException("afterLog boom")
    val throwingLogger = object : LogcatLogger {
      override fun log(priority: LogPriority, tag: String, message: String) {
        throw loggerException
      }
    }
    loggers += throwingLogger
    observer = object : LogcatObserver {
      override fun beforeLog(priority: LogPriority, tag: String) {}
      override fun afterLog(priority: LogPriority, tag: String) {
        throw afterLogException
      }
    }

    val thrown = runCatching { logcat { "Hi" } }.exceptionOrNull()

    assertThat(thrown).isSameAs(loggerException)
    assertThat(thrown!!.suppressedExceptions).containsExactly(afterLogException)
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

  @Test fun `multiple loggers all receive the message`() {
    LogcatLogger.install()
    val logger2 = TestLogcatLogger().apply { loggers += this }
    val logger3 = TestLogcatLogger().apply { loggers += this }

    logcat { "Hi" }

    assertThat(logger.allLogs.map { it.message }).isEqualTo(listOf("Hi"))
    assertThat(logger2.allLogs.map { it.message }).isEqualTo(listOf("Hi"))
    assertThat(logger3.allLogs.map { it.message }).isEqualTo(listOf("Hi"))
  }

  @Test fun `when first logger is not loggable, later loggers still receive message`() {
    LogcatLogger.install()
    logger.shouldLog = false
    val logger2 = TestLogcatLogger().apply { loggers += this }

    logcat { "Hi" }

    assertThat(logger.allLogs).isEmpty()
    assertThat(logger2.allLogs.map { it.message }).isEqualTo(listOf("Hi"))
  }

  @Test fun `only loggable loggers receive the message`() {
    LogcatLogger.install()
    val logger2 = TestLogcatLogger().apply {
      shouldLog = false
      loggers += this
    }
    val logger3 = TestLogcatLogger().apply { loggers += this }

    logcat { "Hi" }

    assertThat(logger.allLogs.map { it.message }).isEqualTo(listOf("Hi"))
    assertThat(logger2.allLogs).isEmpty()
    assertThat(logger3.allLogs.map { it.message }).isEqualTo(listOf("Hi"))
  }

  @Test fun `message lambda is evaluated exactly once with multiple loggers`() {
    LogcatLogger.install()
    val logger2 = TestLogcatLogger().apply { loggers += this }
    val logger3 = TestLogcatLogger().apply { loggers += this }
    var evaluationCount = 0

    logcat { "Hi${++evaluationCount}" }

    assertThat(evaluationCount).isEqualTo(1)
    assertThat(logger.latestLog!!.message).isEqualTo("Hi1")
    assertThat(logger2.latestLog!!.message).isEqualTo("Hi1")
    assertThat(logger3.latestLog!!.message).isEqualTo("Hi1")
  }

  @Test fun `observer beforeLog and afterLog called exactly once with multiple loggers`() {
    LogcatLogger.install()
    val logger2 = TestLogcatLogger().apply { loggers += this }
    var beforeCount = 0
    var afterCount = 0
    observer = object : LogcatObserver {
      override fun beforeLog(priority: LogPriority, tag: String) {
        beforeCount++
      }
      override fun afterLog(priority: LogPriority, tag: String) {
        afterCount++
      }
    }

    logcat { "Hi" }

    assertThat(beforeCount).isEqualTo(1)
    assertThat(afterCount).isEqualTo(1)
  }

  @Test fun `when all loggers not loggable, the message lambda is not invoked`() {
    LogcatLogger.install()
    logger.shouldLog = false
    val logger2 = TestLogcatLogger().apply {
      shouldLog = false
      loggers += this
    }
    var count = 0

    logcat { "Hi${++count}" }

    assertThat(count).isEqualTo(0)
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
