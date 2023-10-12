object Versions {
  /**
   * To change this in the IDE, use `systemProp.square.kotlinVersion=x.y.z` in your
   * `~/.gradle/gradle.properties` file.
   */
  val KotlinCompiler = System.getProperty("square.kotlinVersion") ?: "1.9.10"
}

object Dependencies {
  object Build {
    const val Android = "com.android.tools.build:gradle:8.1.2"
    const val MavenPublish = "com.vanniktech:gradle-maven-publish-plugin:0.15.1"
    val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KotlinCompiler}"
    const val Ktlint = "org.jlleitschuh.gradle:ktlint-gradle:10.2.0"
    const val AndroidXAnnotation = "androidx.annotation:annotation:1.7.0"
    const val BinaryCompatibility = "org.jetbrains.kotlinx:binary-compatibility-validator:0.13.2"
  }

  const val AppCompat = "androidx.appcompat:appcompat:1.2.0"
  const val JUnit = "junit:junit:4.13"
  const val Mockito = "org.mockito:mockito-core:3.4.6"
  const val Robolectric = "org.robolectric:robolectric:4.3.1"
  const val Truth = "com.google.truth:truth:1.1.3"

  object InstrumentationTests {
    const val Core = "androidx.test:core:1.5.0"
    const val Espresso = "androidx.test.espresso:espresso-core:3.3.0"
    const val JUnit = "androidx.test.ext:junit:1.1.2"
    const val Orchestrator = "androidx.test:orchestrator:1.4.2"
    const val Rules = "androidx.test:rules:1.5.0"
    const val Runner = "androidx.test:runner:1.5.0"
    const val UiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
  }
}
