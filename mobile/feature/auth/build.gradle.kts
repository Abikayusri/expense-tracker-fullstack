plugins {
    id("expense.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.auth)
        }
    }
}
