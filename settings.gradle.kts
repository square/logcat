rootProject.name = "logcat"

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

include(
  ":jvm-utils",
  ":logcat",
  ":sample",
  ":sample-jvm"
)
