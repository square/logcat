import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
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
