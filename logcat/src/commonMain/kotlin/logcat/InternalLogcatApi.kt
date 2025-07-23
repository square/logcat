package logcat

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
@RequiresOptIn(
  level = RequiresOptIn.Level.ERROR, message = "This is an internal logcat API that " +
  "should not be used from outside of logcat. No compatibility guarantees are provided." +
  "It is recommended to report your use-case of internal API to logcat issue tracker, " +
  "so stable API could be provided instead"
)
annotation class InternalLogcatApi
