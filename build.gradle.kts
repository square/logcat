import kotlinx.validation.ApiValidationExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

println("Building with Kotlin compiler version ${Versions.KotlinCompiler}")

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
    // For binary compatibility validator.
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
  }

  dependencies {
    classpath(Dependencies.Build.Android)
    classpath(Dependencies.Build.MavenPublish)
    classpath(Dependencies.Build.Kotlin)
    classpath(Dependencies.Build.Ktlint)
    classpath(Dependencies.Build.BinaryCompatibility)
  }
}

// We use JetBrain's Kotlin Binary Compatibility Validator to track changes to our public binary
// APIs.
// When making a change that results in a public ABI change, the apiCheck task will fail. When this
// happens, run ./gradlew apiDump to generate updated *.api files, and add those to your commit.
// See https://github.com/Kotlin/binary-compatibility-validator
apply(plugin = "binary-compatibility-validator")

extensions.configure<ApiValidationExtension> {
  // Ignore all sample projects, since they're not part of our API.
  // Only leaf project name is valid configuration, and every project must be individually ignored.
  // See https://github.com/Kotlin/binary-compatibility-validator/issues/3
  ignoredProjects = mutableSetOf(
    "sample"
  )
}

val isRunningLocally get() = (System.getenv("CI") ?: "false").toBoolean()

subprojects {
  repositories {
    google()
    mavenCentral()
  }

  apply(plugin = "org.jlleitschuh.gradle.ktlint")

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      // Allow warnings when running from IDE, makes it easier to experiment.
      allWarningsAsErrors = !isRunningLocally

      jvmTarget = "1.8"
    }
  }

  // Configuration documentation: https://github.com/JLLeitschuh/ktlint-gradle#configuration
  configure<KtlintExtension> {
    // Prints the name of failed rules.
    verbose.set(true)
    reporters {
      // Default "plain" reporter is actually harder to read.
      reporter(ReporterType.JSON)
    }
  }
}
