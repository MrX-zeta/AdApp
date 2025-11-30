plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.adapp"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
    // Forzar properties JVM para evitar que Netty use sun.misc.Unsafe en tiempo de arranque
    applicationDefaultJvmArgs = listOf(
        "-Dio.netty.noUnsafe=true",
        "-Dio.netty.tryReflectionSetAccessible=false"
    )
}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages) // Para manejo de errores
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    // Soporte para subir archivos (multipart)
    // Ya está incluido en ktor-server-core, no necesita dependencia adicional
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
