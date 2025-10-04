// --- ADD THESE IMPORTS AT THE TOP ---
import java.util.Properties

// --- ADD THIS HELPER TO LOAD android/.env ---
fun loadDotEnv(path: String): Properties {
    val props = Properties()
    val file = rootProject.file(path)
    if (file.exists()) file.inputStream().use { props.load(it) }
    return props
}

// Load env once (points to .env at repo root)
val env = loadDotEnv(".env")
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.MyGmail"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.androidproject"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${env.getProperty("API_BASE_URL", "http://10.0.2.2:8080/api/")}\""
        )
		buildConfigField(
			"String",
			"WS_BASE_URL",
			"\"${env.getProperty("WS_BASE_URL", "http://10.0.2.2:8080/")}\""
		)
        buildConfigField(
            "String",
            "ENV",
            "\"${env.getProperty("ENV", "local")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime)
    implementation(libs.lifecycle.viewmodel)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.11.0")
    annotationProcessor(libs.room.compiler)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}