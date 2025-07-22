import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.binary.compatibility.validator) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.maven.publish) apply false
  alias(libs.plugins.ktlint)
}

// Configuration documentation: https://github.com/JLLeitschuh/ktlint-gradle#configuration
ktlint {
  // Prints the name of failed rules.
  verbose = true
  reporters {
    // Default "plain" reporter is actually harder to read.
    reporter(ReporterType.JSON)
  }
}
