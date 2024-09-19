plugins {
    alias(androidx.plugins.library)
    alias(kotlinx.plugins.android)
}

android {
    namespace = "komari.presentation.core"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    api(libs.material)
}
