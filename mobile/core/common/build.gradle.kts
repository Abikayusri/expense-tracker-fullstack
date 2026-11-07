plugins {
    id("expense.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.lifecycle.viewmodel)
            api(libs.kotlinx.datetime)
        }
    }
}
