@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.binary.compatibility.validator)
  alias(libs.plugins.dokka)
}

kotlin {
  androidTarget {
    publishLibraryVariants("release")
    java {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
      }
    }
  }
  jvm {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_1_8)
    }
  }

  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }

  sourceSets {
    applyDefaultHierarchyTemplate()

    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.atomicfu)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.truthish)
      }
    }

    androidMain {
      dependencies {
        implementation(project(":jvm-utils"))
      }
    }

    jvmMain {
      dependencies {
        implementation(project(":jvm-utils"))
      }
    }
  }
}

android {
  compileSdk = 36

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdk = 17
  }

  buildFeatures {
    buildConfig = false
  }

  namespace = "com.squareup.logcat"
  testNamespace = "com.squareup.logcat.test"
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
  signAllPublications()
  pomFromGradleProperties()

  configure(
    KotlinMultiplatform(javadocJar = JavadocJar.Dokka("dokkaHtml")),
  )
}
