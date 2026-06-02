plugins {
    id("aura-life.android.library")
}

android {
    namespace = "com.drs.auralife.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}