import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

fun getSecret(key: String, default: String): String {
    val secretsFile = rootProject.file("secrets.properties")
    if (!secretsFile.exists()) return default
    val props = Properties()
    props.load(FileInputStream(secretsFile))
    return props.getProperty(key) ?: default
}

val baseUrl = getSecret("baseUrl", "https://default.example.com")

android {
    namespace = "com.drs.auralife.core.network"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}