plugins {
    id("com.android.library")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.drs.auralife.feature.history"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { viewBinding = true }
}

dependencies {
    apply(from = rootProject.file("gradle/deps-feature.gradle"))
    implementation(project(":core:navigation"))
    implementation(project(":domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":data"))
}