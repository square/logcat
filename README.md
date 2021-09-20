# Square Logcat

```kotlin
logcat { "I CAN HAZ LOGZ?" }
```

A tiny Kotlin API for cheap logging on top of Android's normal `Log` class.

## Table of contents

* [Setup](#setup)
* [Usage](#usage)
* [Motivations](#faq)
* [License](#license)

![logo_512.png](assets/logo_512.png)

_This fantastic logo is brought to you by [@rjrjr](https://github.com/rjrjr)._

## Setup

Add the `logcat` dependency to your library or app's `build.gradle` file:

```gradle
dependencies {
  implementation 'com.squareup.logcat:logcat:1.0'
}
```

Install `AndroidLogcatLogger` in `Application.onCreate()`:

```kotlin
import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority.VERBOSE
import logcat.LogcatLogger

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // Log all priorities in debug builds, no-op in release builds.
    if (BuildConfig.DEBUG) {
      LogcatLogger.install(AndroidLogcatLogger(minPriority = VERBOSE))
    }
  }
}
```

## Usage

The `logcat()` function has 3 parameters: an optional priority, an optional tag, and a required
string producing lambda. The lambda is only evaluated if a logger is installed and the logger deems
the priority loggable.

The priority defaults to `LogPriority.DEBUG`.

The tag defaults to the class name of the log call site, without any extra runtime cost. This works
because `logcat()` is an inlined extension function of `Any` and has access to `this` from which
it can extract the class name. If logging from a standalone function which has no `this`, use the
`logcat` overload which requires a tag parameter.

The `logcat()` function does not take a `Throwable` parameter. Instead, the library provides
a Throwable extension function: `Throwable.asLog()` which returns a loggable string.

```kotlin
import logcat.LogPriority.INFO
import logcat.asLog
import logcat.logcat

class MouseController {

  fun play {
    var state = "CHEEZBURGER"
    logcat { "I CAN HAZ $state?" }
    // logcat output: D/MouseController: I CAN HAZ CHEEZBURGER?

    logcat(INFO) { "DID U ASK 4 MOAR INFO?" }
    // logcat output: I/MouseController: DID U ASK 4 MOAR INFO?

    logcat { exception.asLog() }
    // logcat output: D/MouseController: java.lang.RuntimeException: FYLEZ KERUPTED
    //                        at sample.MouseController.play(MouseController.kt:22)
    //                        ...

    logcat("Lolcat") { "OH HI" }
    // logcat output: D/Lolcat: OH HI
  }
}
```

## Motivations

We built this small library to fit the specific needs of the Square
[Point of Sale](https://squareup.com/us/en/point-of-sale) application. We used
[Timber](https://github.com/JakeWharton/timber) heavily before that, and love the simplicity of its
API and the ability of its `DebugTree` to automatically figure out from which class it's being
called and use that class name as its tag. Here are our motivations for replacing it with
`logcat()` in the Square Point of Sale:

- Kotlin support for string interpolation is really nice. We love to use that for logs!
Unfortunately that can be costly and a waste of good CPU if logging is disabled anyway. By using
an inlined string producing lambda, `logcat()` supports string interpolation without any
performance cost when logging is disabled.
- Timber's `DebugTree` captures the calling class name as a tag by creating a stacktrace, which can
be expensive. By making `logcat()` an extension function of `Any`, we can call `this::class.java`
and get the calling context without creating a stacktrace.
- Most of the time, our developers just want to "send something to logcat" without even thinking
about priority. `logcat()` picks "debug" as the right default to provide more consistency across
a large codebase. Making the priority a parameter also means only one method to learn, and you
don't have to learn / think about priorities prior to writing a log. This becomes especially
important when there are several parameters requiring overloads (e.g. in Timber (6 priorities + 1
generic log method) * 3 overloads = 21 methods to choose from).
- The lack of throwable parameter is also intentional. It just creates more overloads and confusion
(e.g. "what's the param order?"), when really logs are about strings and all you need is an easy
way to turn a throwable into a loggable string. Hence `Throwable.asLog()`.
- The name `logcat()` is intentionally boring and identical to the Android command line tool. This
makes it easy to remember and developers know exactly what this does, i.e. log to the local device.
One could setup a custom logger that send logs remotely in release builds, however we do not
recommend doing so: in our experience, remote logs should be distinct in code from local logs and
clearly identified as such, because the volume and performance impact should be very distinct.
- The API for installing a logger is separated out from the API to log, as these operations occur
in very distinct contexts.

## License

<pre>
Copyright 2021 Square Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
