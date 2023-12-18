plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.4.3")
    implementation("org.jetbrains.lets-plot:lets-plot-batik:4.1.0")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}