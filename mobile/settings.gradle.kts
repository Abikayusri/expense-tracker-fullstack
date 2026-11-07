rootProject.name = "expense-mobile"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":androidApp")
include(":shared")
include(":core:common")
include(":core:design-system")
include(":core:data-store")
include(":core:network")
include(":domain:auth")
include(":data:auth")
include(":core:shared-ui")
include(":feature:auth")
