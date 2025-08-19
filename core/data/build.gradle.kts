plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.DAGGER_HILT_ANDROID)
}

apply<SharedLibraryGradlePlugin>()

android {
    namespace = "com.merteroglu286.data"
}

dependencies {

    androidx()
    hilt()
    okHttp()
    retrofit()

    testDeps()
    testImplDeps()
    debugDeps()

}