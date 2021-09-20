package logcat

interface LogcatLogger {

  fun isLoggable(priority: LogPriority) = true

  fun log(
    priority: LogPriority,
    tag: String,
    message: String
  )

  companion object {
    @Volatile
    @PublishedApi
    internal var logger: LogcatLogger = NoLog
      private set

    @Volatile
    private var installed: Throwable? = null

    fun install(logger: LogcatLogger) {
      val loggerName = logger.toString()
      synchronized(this) {
        if (Companion.logger !== NoLog) {
          throw IllegalStateException(
            "Cannot install $loggerName, already installed (see exception cause)", installed
          )
        }
        installed = RuntimeException("$loggerName first installed here")
        Companion.logger = logger
      }
      logcat { "$loggerName installed" }
    }

    fun uninstall() {
      synchronized(this) {
        installed = null
        logger = NoLog
      }
    }
  }

  private object NoLog : LogcatLogger {
    override fun isLoggable(priority: LogPriority) = false

    override fun log(
      priority: LogPriority,
      tag: String,
      message: String
    ) = error("Should never receive any log")
  }
}