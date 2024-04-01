import com.android.build.gradle.TestedExtension

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    kotlin("plugin.serialization")
}


android {

    @Suppress("UNCHECKED_CAST")
    apply(extra["appConfig"] as TestedExtension.() -> Unit)

    namespace = "com.mgws.adownkyi.core"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlinOptions {
        jvmTarget = "17"
    }

}

dependencies {

    // https://github.com/arthenica/ffmpeg-kit
//    implementation("com.arthenica:ffmpeg-kit-full:6.0-2")
    // https://mvnrepository.com/artifact/org.brotli/dec
    implementation(libs.dec)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.powermock.api.mockito2)
    testImplementation(libs.powermock.module.junit4)
}

