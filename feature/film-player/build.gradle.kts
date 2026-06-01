plugins {
    id("aura-life.android.feature")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.drs.auralife.feature.film.player"
    buildFeatures { dataBinding = true }
}

dependencies {
    apply(from = rootProject.file("gradle/deps-feature.gradle"))
    implementation(project(":core:navigation"))
    implementation(project(":domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":data"))
    implementation(project(":feature:film-detail"))
    implementation(project(":feature:history"))
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.ui)
}
