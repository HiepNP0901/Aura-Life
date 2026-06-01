plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly("com.android.tools.build:gradle-api:9.1.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "aura-life.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "aura-life.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
