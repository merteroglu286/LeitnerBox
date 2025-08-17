import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.androidx(){
    implementation(Deps.ANDROIDX_CORE_KTX)
    implementation(Deps.ANDROIDX_LIFECYCLE_RUNTIME_KTX)
    implementation(Deps.ANDROIDX_ACTIVITY_COMPOSE)
    platformImplementation(Deps.ANDROIDX_COMPOSE_BOM)
    implementation(Deps.ANDROIDX_UI)
    implementation(Deps.ANDROIDX_UI_GRAPHICS)
    implementation(Deps.ANDROIDX_UI_TOOLING_PREVIEW)
    implementation(Deps.MATERIAL3)
}

fun DependencyHandler.room(){
    implementation(Deps.ROOM_KTX)
    implementation(Deps.ROOM_RUNTIME)
    ksp(Deps.ROOM_COMPILER)
}

fun DependencyHandler.hilt(){
    implementation(Deps.HILT_ANDROID)
//    implementation(Deps.HILT_COMPOSE)
//    implementation(Deps.HILT_NAVIGATION)
    ksp(Deps.HILT_COMPILER)
//    ksp(Deps.HILT_AGP)
}

fun DependencyHandler.authModule(){
    moduleImplementation(Modules.AUTH)
}

fun DependencyHandler.testDependencies(){
    testImplementation(TestDependencies.JUNIT)
}

fun DependencyHandler.testImplDependencies(){
    androidTestImplementation(TestDependencies.ANDROIDX_JUNIT)
    androidTestImplementation(TestDependencies.ANDROIDX_ESPRESSO_CORE)
    androidTestImplementation(TestDependencies.ANDROIDX_COMPOSE_UI_TEST)
}
fun DependencyHandler.debugDependencies(){
    debugImplementation(Deps.ANDROIDX_UI_TOOLING)
    debugImplementation(TestDependencies.ANDROIDX_UI_TEST_MANIFEST)
}