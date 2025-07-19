import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.binary.compatibility.validator)
}

android {
  compileSdk = 34

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }

  defaultConfig {
    minSdk = 17
    targetSdk = 34
  }

  buildFeatures {
    buildConfig = false
  }

  namespace = "com.squareup.logcat"
  testNamespace = "com.squareup.logcat.test"
}

dependencies {
  implementation(libs.kotlin.stdlib)

  testImplementation(libs.junit)
  testImplementation(libs.mockito.core)
  testImplementation(libs.robolectric)
  testImplementation(libs.truth)
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
  signAllPublications()
  pomFromGradleProperties()

  configure(
    AndroidSingleVariantLibrary(variant = "release", sourcesJar = true, publishJavadocJar = true),
  )
}
