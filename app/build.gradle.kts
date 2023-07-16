plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.dorachat.aop")
}

android {
    namespace = "site.doramusic.app"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        buildFeatures {
            dataBinding = true
        }
    }
    flavorDimensions("app")
    productFlavors {
        // 线上环境
        create("beta") {
            dimension = "app"
            versionNameSuffix = "-beta"
        }
        // 内测/预发环境
        create("alpha") {
            dimension = "app"
            applicationIdSuffix = ".alpha"
            versionNameSuffix = "-alpha"
        }
        // 开发环境
        create("dev") {
            dimension = "app"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
    }
    signingConfigs {
        create("signed") {
            storeFile = File("../doramusic.jks")
            keyAlias = "key0"
            keyPassword = "123456"
            storePassword = "123456"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

kapt {
    generateStubs = true
    correctErrorTypes = true
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.guava:guava:27.0.1-android")
    implementation(platform("com.google.firebase:firebase-bom:31.2.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Dora全家桶
    implementation("com.github.dora4:dcache-android:1.7.9")
    implementation("com.github.dora4:dora:1.1.9")
    implementation("com.github.dora4:dora-arouter-support:1.1")
    implementation("com.github.dora4:dora-apollo-support:1.1")
//    implementation 'com.github.dora4:dora-eventbus-support:1.1'
    implementation("com.github.dora4:dview-toggle-button:1.0")
    implementation("com.github.dora4:dview-alert-dialog:1.0")
    implementation("com.github.dora4:dview-loading-dialog:1.2")
    implementation("com.github.dora4:dview-colors:1.0")
    val map = hashMapOf<String, Any>()
    map["include"] = arrayOf("*.jar", "*.aar")
    map["dir"] = "libs"
    implementation(fileTree(map))
    implementation("com.github.dora4:dview-skins:1.4")
//    implementation 'com.github.dora4:dview-avatar:1.4'
    implementation("com.github.dora4:dview-titlebar:1.9")

    // ARouter
    implementation("com.alibaba:arouter-api:1.5.2")
    kapt("com.alibaba:arouter-compiler:1.5.2")

    implementation("com.github.JackWHLiu:jackknife:75a681cd6f")
    // AspectJ
    implementation("org.aspectj:aspectjrt:1.9.19")
    // leakcanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
    // XXPermissions
    implementation("com.github.getActivity:XXPermissions:18.2")
    // BaseRecyclerViewAdapterHelper
    implementation("io.github.cymchad:BaseRecyclerViewAdapterHelper:3.0.6")
}