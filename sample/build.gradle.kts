plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
}

android {
  compileSdk = 36

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }

  defaultConfig {
    minSdk = 21
    targetSdk = 36
    applicationId = "com.squareup.logcat.sample"
  }

  namespace = "com.squareup.logcat.sample"
  testNamespace = "com.squareup.logcat.sample.test"
}

dependencies {
  implementation(project(":logcat"))
  implementation(project(":sample-jvm"))
  implementation(libs.appcompat)
}
