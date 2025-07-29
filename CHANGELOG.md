Change Log
==========

Next version
-------------

_20XX-XX-XX_


Version 0.4
-------------

_2025-07-29_

* `isLoggable` now takes a priority and a tag, which allows skipping logging based on tags.


Version 0.3
-------------

_2025-07-25_

* Project restructured to support KMP
* New API: `Logcat.observer` which allows tracing / measuring how long logging takes.

Version 0.2.3
-------------

_2025-06-05_

* Multiple `LogcatLogger` instances can now be added to `LoggatLogger.loggers`.
* Breaking change: `LogcatLogger.install()` does not take a `LogcatLogger` instance 
anymore.
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

