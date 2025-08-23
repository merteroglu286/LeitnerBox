plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
}

apply<SharedLibraryGradlePlugin>()

android {
    namespace = "com.merteroglu286.datastore"
}

dependencies {

    dataStore()

    testDeps()
    testImplDeps()
    debugDeps()

}