plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
//    gradlePluginPortal()
}

dependencies{
    api(kotlin("gradle-plugin:2.2.10"))
    implementation("com.android.tools.build:gradle:8.9.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.2.10")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.56.2")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.2.10-2.0.2")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.1.10")

}
