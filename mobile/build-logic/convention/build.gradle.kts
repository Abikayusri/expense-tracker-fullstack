plugins {
    `kotlin-dsl`
}

group = "com.abika.expense.buildlogic"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "expense.kmp.library"
            implementationClass = "com.abika.expense.buildlogic.KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "expense.kmp.compose"
            implementationClass = "com.abika.expense.buildlogic.KmpComposeConventionPlugin"
        }
        register("kmpKoin") {
            id = "expense.kmp.koin"
            implementationClass = "com.abika.expense.buildlogic.KmpKoinConventionPlugin"
        }
        register("kmpDomain") {
            id = "expense.kmp.domain"
            implementationClass = "com.abika.expense.buildlogic.KmpDomainConventionPlugin"
        }
        register("kmpData") {
            id = "expense.kmp.data"
            implementationClass = "com.abika.expense.buildlogic.KmpDataConventionPlugin"
        }
        register("kmpFeature") {
            id = "expense.kmp.feature"
            implementationClass = "com.abika.expense.buildlogic.KmpFeatureConventionPlugin"
        }
    }
}
