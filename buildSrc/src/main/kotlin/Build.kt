sealed class Build {
    open val isMinifyEnabled = false
    open val enableUnitTestCoverage = false
    open val isDebuggable = false
    open val applicationIdSuffix = ""
    open val versionNameSuffix = ""

    object Debug : Build() {
        override val isMinifyEnabled = false
        override val enableUnitTestCoverage = true
        override val isDebuggable = true
        override val applicationIdSuffix = ".debug"
        override val versionNameSuffix = "-DEBUG"
    }

    object Release : Build() {
        override val isMinifyEnabled = true
        override val enableUnitTestCoverage = false
        override val isDebuggable = false
    }
}