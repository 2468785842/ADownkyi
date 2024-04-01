pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "ADownKyi"
include(":app")
include(":core")
//include(":datastore")
include(":datastore:annotations")
include(":datastore:compiler")
