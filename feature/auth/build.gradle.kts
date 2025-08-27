plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.DAGGER_HILT_ANDROID)
}

apply<SharedLibraryGradlePlugin>()

android {
    namespace = "com.merteroglu286.auth"
}

dependencies {

    domainModule()
    dataModule()

    androidx()
    hilt()
    retrofit()
    testDeps()
    testImplDeps()
    debugDeps()

}