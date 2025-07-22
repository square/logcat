package logcat

import logcat.LogPriority.ASSERT
import logcat.LogPriority.DEBUG
import logcat.LogPriority.ERROR
import logcat.LogPriority.INFO
import logcat.LogPriority.VERBOSE
import logcat.LogPriority.WARN
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class LogcatObjectTest {
  private lateinit var logger: TestLogcatLogger

  @BeforeTest
  fun before() {
    LogcatLogger.install()
    logger = TestLogcatLogger()
    LogcatLogger.loggers += logger
  }

  @AfterTest
  fun after() {
    LogcatLogger.uninstall()
    LogcatLogger.loggers -= logger
  }

  // don't add another test above this - it depends on the line number
  @Test
  fun `Log exception`() {
    val e = IllegalStateException("SOMETHING BROKE")
    logcat.e { e.asLog() }
    val log = logger.latestLog
    assertEquals(expected = ERROR, log?.priority)
    assertEquals(expected = "LogcatObjectTest", log?.tag)
    val msg = log?.message.orEmpty()
    assertContains(msg, "java.lang.IllegalStateException: SOMETHING BROKE")
    assertContains(msg, "at logcat.LogcatObjectTest.Log exception(LogcatShortcutsTest.kt:34)")
  }

  @Test
  fun `Logcat shortcuts`() {
    logcat.v { "verbose" }
    logger.assertLatest(priority = VERBOSE, tag = "LogcatObjectTest", message = "verbose")

    logcat.d("DebugTag") { "debug" }
    logger.assertLatest(priority = DEBUG, tag = "DebugTag", message = "debug")

    logcat.i { "info" }
    logger.assertLatest(priority = INFO, tag = "LogcatObjectTest", message = "info")

    logcat.w(tag = null) { "warning" }
    logger.assertLatest(priority = WARN, tag = "LogcatObjectTest", message = "warning")

    logcat.assert { "assert" }
    logger.assertLatest(priority = ASSERT, tag = "LogcatObjectTest", message = "assert")
  }

  @Test
  fun `Grab tag from the wrapper class inside apply block`() {
    val myString = "abc-123"

    myString.apply {
      logcat.i { "my log message" }
    }

    logger.assertLatest(priority = INFO, tag = "String", message = "my log message")
  }
}
