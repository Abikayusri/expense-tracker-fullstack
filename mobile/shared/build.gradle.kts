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
            implementation(projects.core.common)
            implementation(projects.core.designSystem)
            implementation(libs.koin.compose)
        }
    }
}
