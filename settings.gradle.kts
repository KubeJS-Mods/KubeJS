plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val modName: String by extra
val minecraftVersion: String by extra
rootProject.name = "$modName-$minecraftVersion"