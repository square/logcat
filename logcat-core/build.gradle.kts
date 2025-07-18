plugins {
  kotlin("jvm")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  testImplementation(Dependencies.JUnit)
  testImplementation(Dependencies.Mockito)
  testImplementation(Dependencies.Truth)
}
