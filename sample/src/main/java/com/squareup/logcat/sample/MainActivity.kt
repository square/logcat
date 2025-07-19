package com.squareup.logcat.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.logcat.sample.jvm.HelloJvm
import logcat.LogPriority.INFO
import logcat.asLog
import logcat.logcat
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.main)

    logcat { "Created" }

    HelloJvm.logHello()

    logcat(INFO) { "I like turtles" }

    findViewById<View>(R.id.log_stacktrace).setOnClickListener {
      logcat { RuntimeException("Logged Stacktrace").asLog() }
    }
  }
}
