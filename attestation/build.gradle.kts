plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.google.auto.value:auto-value-annotations:1.10.1")
    implementation("com.google.protobuf:protobuf-java:3.21.12")
    implementation("com.google.errorprone:error_prone_annotations:2.18.0")
    annotationProcessor("com.google.auto.value:auto-value:1.10.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
