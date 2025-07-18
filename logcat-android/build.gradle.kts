plugins {
  id("com.android.library")
  kotlin("android")
  id("com.vanniktech.maven.publish.base")
}

android {
  compileSdk = 34

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdk = 17
    targetSdk = 34
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    buildConfig = false
  }

  testOptions {
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }

  namespace = "com.squareup.logcat"
  testNamespace = "com.squareup.logcat.test"
}

dependencies {
  api(project(":logcat-core"))

  compileOnly(Dependencies.Build.AndroidXAnnotation)

  testImplementation(Dependencies.JUnit)
  testImplementation(Dependencies.Mockito)
  testImplementation(Dependencies.Robolectric)
  testImplementation(Dependencies.Truth)

  androidTestImplementation(Dependencies.InstrumentationTests.Core)
  androidTestImplementation(Dependencies.InstrumentationTests.Espresso)
  androidTestImplementation(Dependencies.InstrumentationTests.JUnit)
  androidTestImplementation(Dependencies.InstrumentationTests.Rules)
  androidTestImplementation(Dependencies.InstrumentationTests.Runner)
  androidTestImplementation(Dependencies.InstrumentationTests.UiAutomator)
  androidTestImplementation(Dependencies.Truth)
  androidTestImplementation(Dependencies.AppCompat)

  androidTestUtil(Dependencies.InstrumentationTests.Orchestrator)
}
