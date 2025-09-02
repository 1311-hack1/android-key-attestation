plugins {
    application
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "3.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:3.0.0")
    implementation("io.ktor:ktor-server-netty-jvm:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // existing dependencies from repo (attestation verifier)
    implementation(project(":attestation"))
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}
