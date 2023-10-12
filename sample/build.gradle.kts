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
    applicationId = "com.squareup.logcat.sample"
    namespace = "com.squareup.logcat.sample"
  }
}

dependencies {
  implementation(project(":logcat"))
  implementation(Dependencies.AppCompat)
}
