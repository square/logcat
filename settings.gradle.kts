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
    ":logcat",
    ":sample"
)
