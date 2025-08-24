plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.GOOGLE_PROTOBUF)
}

apply<SharedLibraryGradlePlugin>()

android {
    namespace = "com.merteroglu286.protodatastore"

    protobuf {
        protoc {
            artifact = Deps.PROTOC
        }
        generateProtoTasks {
            all().forEach { task ->
                task.plugins {
                    create("kotlin").apply {
                        option("lite")
                    }
                }
                task.plugins {
                    create("java").apply {
                        option("lite")
                    }
                }
            }
        }
    }
}

dependencies {

    protoDataStore()

    testDeps()
    testImplDeps()
    debugDeps()

}