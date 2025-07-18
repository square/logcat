plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdk = 34

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdk = 21
    targetSdk = 34
    applicationId = "com.squareup.logcat.sample"
  }

  namespace = "com.squareup.logcat.sample"
  testNamespace = "com.squareup.logcat.sample.test"
}

dependencies {
  implementation(project(":logcat-android"))
  implementation(Dependencies.AppCompat)
}
