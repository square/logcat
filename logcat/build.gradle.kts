plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

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
    }
    nodejs {
    }
    binaries.executable()
  }
  android()
  ios()
  watchos()
  tvos()
  macosX64() {
    binaries {
      sharedLib {
        baseName = "logcat"
      }
      framework {
        baseName = "Logcat"
      }
    }
  }
  macosArm64()
  linuxArm64()
  linuxArm32Hfp()
  linuxMips32()
  linuxMipsel32()
  linuxX64()
  mingwX64()
  mingwX86()

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(Dependencies.Coroutines)
      }
    }
    val jvmMain by getting
    val jvmTest by getting {
      dependencies {
        implementation(Dependencies.Truth)
      }
    }
    val jsMain by getting
    val jsTest by getting
    val nativeMain by creating {
      dependsOn(commonMain)
    }
    val nativeTest by creating {
      dependsOn(commonTest)
    }
    val macosX64Main by getting {
      dependsOn(nativeMain)
    }
    val macosX64Test by getting {
      dependsOn(nativeTest)
    }
    val macosArm64Main by getting {
      dependsOn(nativeMain)
    }
    val macosArm64Test by getting {
      dependsOn(nativeTest)
    }
    val linuxX64Main by getting {
      dependsOn(nativeMain)
    }
    val linuxX64Test by getting {
      dependsOn(nativeTest)
    }
    val linuxArm64Main by getting {
      dependsOn(nativeMain)
    }
    val linuxArm64Test by getting {
      dependsOn(nativeTest)
    }
    val linuxArm32HfpMain by getting {
      dependsOn(nativeMain)
    }
    val linuxArm32HfpTest by getting {
      dependsOn(nativeTest)
    }
    val linuxMips32Main by getting {
      dependsOn(nativeMain)
    }
    val linuxMips32Test by getting {
      dependsOn(nativeTest)
    }
    val linuxMipsel32Main by getting {
      dependsOn(nativeMain)
    }
    val linuxMipsel32Test by getting {
      dependsOn(nativeTest)
    }
    val mingwX64Main by getting {
      dependsOn(nativeMain)
    }
    val mingwX64Test by getting {
      dependsOn(nativeTest)
    }
    val mingwX86Main by getting {
      dependsOn(nativeMain)
    }
    val mingwX86Test by getting {
      dependsOn(nativeTest)
    }
    val iosMain by getting {
      dependsOn(nativeMain)
    }
    val iosTest by getting {
      dependsOn(nativeTest)
    }
    val watchosMain by getting {
      dependsOn(nativeMain)
    }
    val watchosTest by getting {
      dependsOn(nativeTest)
    }
    val tvosMain by getting {
      dependsOn(nativeMain)
    }
    val tvosTest by getting {
      dependsOn(nativeTest)
    }
    val androidMain by getting {
      dependsOn(jvmMain)
    }
    val androidTest by getting {
      dependencies {
        implementation(Dependencies.JUnit)
      }
    }
  }
}

android {
  compileSdk = 30
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdk = 14
    targetSdk = 30
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    buildConfig = false
  }

  testOptions {
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }
}
