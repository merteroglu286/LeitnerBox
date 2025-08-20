plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.DAGGER_HILT_ANDROID)
}

apply<SharedLibraryGradlePlugin>()

android {
    namespace = "com.merteroglu286.auth"
}

dependencies {

    androidx()
    hilt()
    hilt()
    testDeps()
    testImplDeps()
    debugDeps()

}