plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.drs.auralife.domain"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation("javax.inject:javax.inject:1")
    implementation(libs.kotlinx.coroutines.core)
}
