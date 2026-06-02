plugins {
    id("aura-life.android.library")
}

android {
    namespace = "com.drs.auralife.core.navigation"
}

dependencies {
    implementation(libs.navigation.fragment.ktx)
}