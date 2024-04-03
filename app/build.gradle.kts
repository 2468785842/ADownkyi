import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    id("com.google.protobuf")

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")

    kotlin("plugin.serialization")
}

android {

    @Suppress("UNCHECKED_CAST")
    apply(extra["appConfig"] as TestedExtension.() -> Unit)

    namespace = "com.mgws.adownkyi"

    defaultConfig {
        applicationId = "com.mgws.adownkyi"
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.2"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = false
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    applicationVariants.all {
        outputs.all {
            val ver = defaultConfig.versionName
            val minSdk =
                project.extensions.getByType(BaseAppModuleExtension::class.java).defaultConfig.minSdk
            val abi = filters.find { it.filterType == "ABI" }?.identifier ?: "all"
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "${rootProject.name}-$ver-${abi}-sdk$minSdk.apk"
        }
    }

}

dependencies {

    implementation(project(":core"))
    implementation(project(":datastore:annotations"))
    ksp(project(":datastore:compiler"))

    implementation(libs.androidx.documentfile)

    implementation(libs.coil.compose)

    implementation(libs.kotlinx.serialization.protobuf)

    /*---------------------------------Hilt-----------------------------------*/
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    /*---------------------------Kotlin Coroutines----------------------------*/
    implementation(libs.kotlinx.coroutines.android)

    /*-------------------------------Android X--------------------------------*/
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.datastore)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    /*---------------------------------TEST-----------------------------------*/
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}