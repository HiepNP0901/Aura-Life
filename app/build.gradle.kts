plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xannotation-default-target=param-property")
    }
}

android {
    namespace = "com.drs.auralife"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.drs.auralife"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Android Core & UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material) // Material Design Components
    implementation(libs.androidx.recyclerview)

    // --- Jetpack Compose ---
    implementation(platform(libs.compose.bom))
    implementation(libs.compose)

    // --- Media ---
    implementation(libs.androidx.media)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.ui)

    // --- Lifecycle & Data ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.work.runtime.ktx)

    // --- Networking ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // --- Hilt ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)

    // --- Room ---
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // --- Firebase ---
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // --- Other ---
    implementation(libs.glide.core)
    implementation(libs.glide.transformations)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    annotationProcessor(libs.compiler)
}

// KtLint configuration
ktlint {
    version = "1.5.0"
    verbose.set(true)
    android.set(true)
    filter {
        exclude { element -> element.file.path.contains("generated/") }
    }
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
