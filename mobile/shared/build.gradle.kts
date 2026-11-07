plugins {
    id("expense.kmp.compose")
    id("expense.kmp.koin")
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.network)
            implementation(projects.core.common)
            implementation(projects.core.designSystem)
            implementation(projects.core.sharedUi)
            implementation(projects.core.dataStore)
            implementation(projects.domain.auth)
            implementation(projects.data.auth)
            implementation(projects.feature.auth)
            implementation(projects.data.category)
            implementation(projects.feature.category)
            implementation(projects.data.transaction)
            implementation(projects.feature.transaction)
            implementation(projects.data.summary)
            implementation(projects.feature.summary)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.lifecycle.viewmodel.navigation3)
            implementation(libs.navigation3.ui)
            implementation(libs.ktor.monitor.logging)
        }
    }
}
