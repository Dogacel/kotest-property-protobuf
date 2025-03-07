plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("com.gradle.develocity") version ("3.19.2")

    id("org.jetbrains.dokka") version "2.0.0" apply false
    id("org.jetbrains.kotlinx.kover") version "0.9.1" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0" apply false
    id("com.google.protobuf") version "0.9.4" apply false
}

rootProject.name = "kotest-property-protobuf"
include("lib")

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/terms-of-service")
        termsOfUseAgree.set("yes")
    }
}
