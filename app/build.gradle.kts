plugins {
//    alias(libs.plugins.android.application)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.yh04.joyfulmindapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yh04.joyfulmindapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation("commons-io:commons-io:2.4")

    implementation("androidx.navigation:navigation-fragment:2.5.3")

    implementation("androidx.navigation:navigation-ui:2.5.3")

    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore-ktx:23.0.3")
    implementation("com.google.firebase:firebase-storage")
    implementation ("com.google.firebase:firebase-firestore:21.4.3")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("androidx.viewpager2:viewpager2:1.1.0-beta01")
}
