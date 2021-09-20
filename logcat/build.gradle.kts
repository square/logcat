import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.library")
  kotlin("android")
  id("com.vanniktech.maven.publish")
}

android {
  compileSdkVersion(30)

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdkVersion(14)
    targetSdkVersion(30)
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    buildConfig = false
  }

  testOptions {
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOfNotNull(
      // allow-jvm-ir-dependencies is required to consume binaries built with the IR backend.
      // It doesn't change the bytecode that gets generated for this module.
      "-Xallow-jvm-ir-dependencies",
      "-Xopt-in=kotlin.RequiresOptIn"
    )
  }
}

dependencies {

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
