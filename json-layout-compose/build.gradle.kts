import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serializationPlugin)
    id("module.publication")
    id("io.github.ttypic.swiftklib") version "0.6.3"
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser{
            testTask {
                useKarma{
                    useChrome()
                }
            }
        }
        binaries.executable()
    }

    js {
        browser()
        binaries.executable()
    }
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    /*
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
     */

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.media3.exoplayer.hls)

            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.androidx.activity.compose)
            //implementation(libs.github.mirzemehdi.google)
            //implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.25")
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.coil.compose) // Check for latest version
                implementation(libs.coil.network.ktor) // For network images
                implementation(libs.ktor.client.core)
                //api(libs.kmp.compose.webview)
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.system.lambda)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                //implementation(libs.ktor.client.core.wasmJs)

                implementation(npm("currency-formatter", "1.5.9"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.java)
                implementation("org.slf4j:slf4j-simple:2.0.13")
            }
        }
    }
    /*
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.compilations {
            val main by getting {
                cinterops {
                    create("HelloSwift")
                    create("Utils")
                }
            }
        }
    }

     */
}

android {
    namespace = "id.dreamfighter.kmp.json.layout.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

swiftklib {
    create("HelloSwift") {
        path = file("native/HelloSwift")
        packageName("com.ttypic.objclibs.greeting")
    }
    create("Utils") {
        path = file("native/Utils")
        packageName("id.dreamfighter.multiplatform.swift")
    }
}