plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.DAGGER_HILT_ANDROID)
}

apply<SharedLibraryGradlePlugin>()

android {
    namespace = "com.merteroglu286.data"
}

dependencies {

    protoDataStoreModule()

    androidx()
    hilt()
    okHttp()
    retrofit()
    dataStore()

    testDeps()
    testImplDeps()
    debugDeps()

}