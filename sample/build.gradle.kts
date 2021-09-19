plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdkVersion(30)

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(30)
    applicationId = "com.squareup.logcat.sample"
  }
}

dependencies {
  implementation(project(":logcat"))
  implementation(Dependencies.AppCompat)
}
