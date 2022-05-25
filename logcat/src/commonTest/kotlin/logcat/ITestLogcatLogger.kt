package logcat

/**
 * Extension of the LogcatLogger interface provided in the logcat library that
 * provides access to `latestLog` for inspection during tests.
 */
interface ITestLogcatLogger : LogcatLogger {
  var latestLog: Log?
}
