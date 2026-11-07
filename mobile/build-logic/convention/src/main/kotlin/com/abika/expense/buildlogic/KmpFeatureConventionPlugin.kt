package com.abika.expense.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("expense.kmp.compose")
        pluginManager.apply("expense.kmp.koin")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(project(":core:common"))
                implementation(project(":core:design-system"))
                implementation(project(":core:shared-ui"))
                implementation(libs.lib("koin-compose"))
                implementation(libs.lib("koin-compose-viewmodel"))
                implementation(libs.lib("lifecycle-viewmodel-compose"))
                implementation(libs.lib("lifecycle-runtime-compose"))
                implementation(libs.lib("navigation3-ui"))
            }
        }
    }
}
