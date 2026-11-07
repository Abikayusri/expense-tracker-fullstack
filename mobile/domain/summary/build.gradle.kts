plugins {
    id("expense.kmp.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.domain.category)
        }
    }
}
