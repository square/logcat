Change Log
==========

Version 0.2
-------------

_2025-05-18_

* Multiple `LogcatLogger` instances can now be added to `LoggatLogger.loggers`.
* Breaking change: `LogcatLogger.install()` does not take a `LogcatLogger` instance 
anymore and instead takes an optional Executor to control the thread on which messages
are evaluated.
* Android libraries that want to use `logcat` should continue to use
`AndroidLogcatLogger.installOnDebuggableApp()` which checks if already installed so as not to
override any app specific set up.
* Changed the `minPriority` default parameter value of
`AndroidLogcatLogger.installOnDebuggableApp()` from `DEBUG` to `VERBOSE`.
* Added `@JvmStatic` and removed `Kt` from class name (`LogcatKt` => `Logcat`) so that APIs are more Java friendly (this remains primarily a Kotlin focused APIs but this change helps with codebases that still have some Java code hanging around).


Version 0.1
-------------

_2021-09-21_

Initial alpha release.

* Everything is new!

