plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

android {
    namespace = "com.example.dalcommunity"
    compileSdk = 34
    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        //exclude 'META-INF/DEPENDENCIES'
        resources.excludes.add("META-INF/LICENSE")
        //exclude 'META-INF/LICENSE'
        resources.excludes.add("META-INF/LICENSE.txt")
        //exclude 'META-INF/LICENSE.txt'
        resources.excludes.add("META-INF/license.txt")
        //exclude 'META-INF/license.txt'
        resources.excludes.add("META-INF/NOTICE")
        //exclude 'META-INF/NOTICE'
        resources.excludes.add("META-INF/NOTICE.txt")
        //exclude 'META-INF/NOTICE.txt'
        resources.excludes.add("META-INF/notice.txt")
        //exclude 'META-INF/notice.txt'
        resources.excludes.add("META-INF/ASL2.0")
        //exclude 'META-INF/ASL2.0'
        resources.excludes.add("META-INF/*.kotlin_module")
        exclude("META-INF/*.kotlin_module")
    }

    defaultConfig {
        applicationId = "com.example.dalcommunity"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    val fragment_version = "1.6.2"
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.fragment:fragment:$fragment_version")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation ("com.squareup.picasso:picasso:2.8")


    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.robolectric:robolectric:4.12")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.1.0")
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation ("com.google.firebase:firebase-messaging:23.4.1")
    debugImplementation("androidx.fragment:fragment-testing:$fragment_version")
    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")



}