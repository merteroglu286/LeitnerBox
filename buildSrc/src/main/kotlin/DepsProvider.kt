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

fun DependencyHandler.dataStore(){
    implementation(Deps.DATA_STORE)
    implementation(Deps.KOTLIN_COLLECTIONS)
    implementation(Deps.KOTLIN_SERIALIZATION)
}

fun DependencyHandler.protoDataStore(){
    implementation(Deps.DATA_STORE)
    implementation(Deps.PROTOBUF_JAVA_LITE)
    implementation(Deps.PROTOBUF_KOTLIN_LITE)
}

fun DependencyHandler.hilt(){
    implementation(Deps.HILT_ANDROID)
//    implementation(Deps.HILT_COMPOSE)
//    implementation(Deps.HILT_NAVIGATION)
    ksp(Deps.HILT_COMPILER)
//    ksp(Deps.HILT_AGP)
}

fun DependencyHandler.okHttp(){
    implementation(Deps.OKHTTP)
    implementation(Deps.OKHTTP_LOGGING_INTERCEPTOR)
}

fun DependencyHandler.retrofit(){
    implementation(Deps.RETROFIT)
    implementation(Deps.RETROFIT_CONVERTER_GSON)
}

fun DependencyHandler.dataModule(){
    moduleImplementation(Modules.DATA)
}

fun DependencyHandler.dataStoreModule(){
    moduleImplementation(Modules.DATA_STORE)
}

fun DependencyHandler.protoDataStoreModule(){
    moduleImplementation(Modules.PROTO_DATA_STORE)
}

fun DependencyHandler.authModule(){
    moduleImplementation(Modules.AUTH)
}

fun DependencyHandler.testDeps(){
    testImplementation(TestDependencies.JUNIT)
}

fun DependencyHandler.testImplDeps(){
    androidTestImplementation(TestDependencies.ANDROIDX_JUNIT)
    androidTestImplementation(TestDependencies.ANDROIDX_ESPRESSO_CORE)
    androidTestImplementation(TestDependencies.ANDROIDX_COMPOSE_UI_TEST)
}
fun DependencyHandler.debugDeps(){
    debugImplementation(Deps.ANDROIDX_UI_TOOLING)
    debugImplementation(TestDependencies.ANDROIDX_UI_TEST_MANIFEST)
}