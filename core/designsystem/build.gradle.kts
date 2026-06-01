plugins {
    id("com.android.library")
}

android {
    namespace = "com.drs.auralife.core.designsystem"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
}


