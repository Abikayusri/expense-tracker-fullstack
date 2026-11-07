plugins {
    id("expense.kmp.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.datastore.preferences.core)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.android)
        }
    }
}
