import com.android.build.gradle.TestedExtension

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false

    id("com.google.dagger.hilt.android") version "2.50" apply false

    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.protobuf") version "0.9.4" apply false

    kotlin("jvm") version "1.9.22" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    val appConfig: TestedExtension.() -> Unit = {
        // 设置SDK版本
        compileSdkVersion(34)

        // 定义缺省配置
        defaultConfig {
            minSdk = 29
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        signingConfigs {
            create("release") {
                storeFile = file(
                    "${projectDir.parent}${File.separator}keystores${File.separator}release-keystore.jks"
                )
                storePassword = "171328"
                keyAlias = "key0"
                keyPassword = "171328"
            }
        }

        buildTypes {
            get("release").apply {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("release")
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        // 定义公共依赖
        dependencies {
            add("implementation", libs.androidx.core.ktx)

            add("testImplementation", "junit:junit:4.13.2")

            add("androidTestImplementation", "androidx.test.ext:junit:1.1.5")
            add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.5.1")

        }

    }
    extra["appConfig"] = appConfig
}

subprojects {
    tasks.withType(Test::class.java).configureEach {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        jvmArgs = jvmArgs!! + listOf(
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
        )
    }
}