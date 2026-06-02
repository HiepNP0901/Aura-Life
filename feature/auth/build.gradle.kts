plugins {
    id("aura-life.android.feature")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.drs.auralife.feature.auth"
}

dependencies {
    apply(from = rootProject.file("gradle/deps-feature.gradle"))
    implementation(project(":core:navigation"))
    implementation(project(":domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":data"))
}