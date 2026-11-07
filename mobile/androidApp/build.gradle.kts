plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

// Firebase is optional for local development: without google-services.json the app runs and simply gets no push token.
if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
}

android {
    namespace = "com.abika.expense.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.abika.expense"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
        val apiPort = providers.gradleProperty("expense.apiPort").get()
        // 10.0.2.2 is the host machine as seen from the Android emulator.
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:$apiPort\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(projects.domain.auth)
    implementation(projects.core.dataStore)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}
