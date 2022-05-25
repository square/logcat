plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

group = "logcatx"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  gradlePluginPortal()
  google()
}

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
  }
  js(IR) {
    browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }
    binaries.executable()
  }
  macosX64("native") {
    binaries {
      sharedLib {
        baseName = "logcat"
      }
    }
  }
  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux" -> linuxX64("native")
    isMingwX64 -> mingwX64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  android()
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val jvmMain by getting
    val jvmTest by getting {
      dependencies {
        implementation("com.google.truth:truth:1.1.3")
      }
    }
    val jsMain by getting
    val jsTest by getting
    val nativeMain by getting
    val nativeTest by getting
    val androidMain by getting {
      dependencies {
        implementation("com.google.android.material:material:1.2.1")
      }
    }
    val androidTest by getting {
      dependencies {
        implementation("junit:junit:4.13")
      }
    }
  }
}

android {
  compileSdkVersion(30)
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdkVersion(14)
    targetSdkVersion(30)
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    buildConfig = false
  }

  testOptions {
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }
}
