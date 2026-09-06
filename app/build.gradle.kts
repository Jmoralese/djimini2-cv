import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Clave DJI fuera de Git: local.properties (dji.appKey) o variable DJI_APP_KEY.
// Sin clave, la variante real compila y muestra un error claro al conectar.
val djiAppKey: String = run {
    val localProperties = Properties()
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { localProperties.load(it) }
    }
    localProperties.getProperty("dji.appKey")
        ?: System.getenv("DJI_APP_KEY")
        ?: ""
}

android {
    namespace = "io.github.jmoralese.djimini2cv"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.jmoralese.djimini2cv"
        minSdk = 24
        // DJI V4.18 declara adaptacion hasta Android 14; subirlo exige ensayo real (D-007).
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
    }

    flavorDimensions += "drone"
    productFlavors {
        create("sim") {
            dimension = "drone"
            applicationIdSuffix = ".sim"
            versionNameSuffix = "-sim"
        }
        create("real") {
            dimension = "drone"
            // El SDK DJI 4.18 solo incluye bibliotecas nativas ARM.
            ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
            manifestPlaceholders["DJI_APP_KEY"] = djiAppKey
        }
    }

    buildTypes {
        release {
            // Sin minificar hasta validar las reglas R8 que exige el SDK DJI.
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        // La proteccion SecNeo del SDK DJI requiere bibliotecas nativas extraidas.
        jniLibs.useLegacyPackaging = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // DJI unicamente en la variante real (docs/INTEGRACION_DJI.md).
    // Los layouts internos del AAR DJI exigen appcompat y constraintlayout.
    "realImplementation"(libs.androidx.appcompat)
    "realImplementation"(libs.androidx.constraintlayout)
    "realImplementation"(libs.dji.runtime) {
        // Correccion de lente de Mavic 2 Pro/Zoom; innecesaria para Mini 2.
        exclude(group = "com.dji", module = "library-anti-distortion")
    }
    "realCompileOnly"(libs.dji.provided)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
