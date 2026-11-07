plugins {
    id("expense.kmp.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.domain.category)
        }
    }
}
