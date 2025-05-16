import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
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
  }

  dependencies {
    classpath(Dependencies.Build.Android)
    classpath(Dependencies.Build.MavenPublish)
    classpath(Dependencies.Build.Kotlin)
    classpath(Dependencies.Build.Ktlint)
    classpath(Dependencies.Build.BinaryCompatibility)
    // Required for the gradle-maven-publish-plugin plugin.
    // See https://github.com/vanniktech/gradle-maven-publish-plugin/issues/205.
    classpath(Dependencies.Build.Dokka)
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

// See https://stackoverflow.com/questions/25324880/detect-ide-environment-with-gradle
val isRunningFromIde get() = project.properties["android.injected.invoked.from.ide"] == "true"

subprojects {
  repositories {
    google()
    mavenCentral()
  }

  apply(plugin = "org.jlleitschuh.gradle.ktlint")

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {
      publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
      signAllPublications()
      pomFromGradleProperties()

      configure(
        AndroidSingleVariantLibrary(variant = "release", sourcesJar = true, publishJavadocJar = true),
      )
    }
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      // Allow warnings when running from IDE, makes it easier to experiment.
      if (!isRunningFromIde) {
        allWarningsAsErrors = true
      }

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
