package com.abika.expense.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("expense.kmp.koin")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(project(":core:common"))
                implementation(project(":core:network"))
                implementation(project(":core:data-store"))
                implementation(libs.lib("ktor-client-core"))
                implementation(libs.lib("kotlinx-serialization-json"))
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(libs.lib("ktor-client-mock"))
            }
        }
    }
}
