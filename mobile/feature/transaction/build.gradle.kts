plugins {
    id("expense.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.transaction)
            implementation(projects.domain.category)
        }
    }
}
