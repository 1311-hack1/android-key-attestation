rootProject.name = "android-key-attestation"

include(":attestation")

// Define the attestation module path
project(":attestation").projectDir = file("attestation")
