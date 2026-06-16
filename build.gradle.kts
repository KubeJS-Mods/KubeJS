import com.almostreliable.almostgradle.dependency.LoadingMode

plugins {
	id("net.neoforged.moddev") version "2.0.138"
	id("com.almostreliable.almostgradle") version "2.1.1"
	id("idea")
	// id("me.shedaniel.unified-publishing") version "0.1.+"
}

val runningInCI = System.getenv("CI").toBoolean()

almostgradle.setup {
	javaVersion = 25

	launchArgs {
		loggingLevel = "INFO"
		// mixinDebugOutput = true
	}

	dataGen = false

	splitRunDirs = true
	withAccessTransformerValidation = !runningInCI

	tests {
		enabled = true
		testMod = true
		gameTests = true
	}

	recipeViewers {
		emi {
			runConfig = true
			mode = LoadingMode.API
			version = "1.1.22"
			minecraftVersion = "1.21.1"
		}

		rei {
			runConfig = true
			mode = LoadingMode.API
			version = "21.11.814"
			minecraftVersion = "1.21.1"
		}

		jei {
			runConfig = true
			mode = LoadingMode.API
			version = "29.6.2.31"
		}
	}
}

val rhinoVersion: String by project
val tinyServerVersion: String by project
val gifLibVersion: String by project
val batVersion: String by project

println("Building version: ${project.version}")

neoForge {
	interfaceInjectionData {
		from(file("interfaces.json"))
		publish(file("interfaces.json"))
	}

	runs {
		// AlmostGradle's gametest run launches as a plain server here; use Neo's headless type and run only our tests.
		matching { it.name == "gametest" }.configureEach {
			type.set("gameTestServer")
			programArgument("--tests")
			programArgument("testmod:*")
		}
	}
}

repositories {
	maven {
		setUrl("https://maven.shedaniel.me/")
		content {
			includeGroup("me.shedaniel")
			includeGroup("me.shedaniel.cloth")
			includeGroup("dev.architectury")
		}
	}

	maven {
		setUrl("https://maven.latvian.dev/releases")
		content {
			includeGroup("dev.latvian.mods")
			includeGroup("dev.latvian.apps")
		}
	}

	maven {
		setUrl("https://maven.latvian.dev/mirror")
		content {
			includeGroup("dev.architectury")
			includeGroup("me.shedaniel")
			includeGroup("me.shedaniel.cloth")
			includeGroup("net.darkhax.bookshelf")
			includeGroup("net.darkhax.gamestages")
			includeGroup("com.github.rtyley")
		}
	}
	mavenCentral()
}

dependencies {
	api("dev.latvian.mods:rhino:$rhinoVersion") { isTransitive = false }

	// not updated to 26.1 yet
	/*compileOnly("dev.architectury:architectury-neoforge:$archVersion")*/

	jarJar(implementation("dev.latvian.apps:tiny-java-server") {
		version {
			strictly("[$tinyServerVersion,)")
			prefer(tinyServerVersion)
		}
	})
	jarJar(implementation("com.github.rtyley:animated-gif-lib-for-java") {
		version {
			strictly("[animated-gif-lib-$gifLibVersion,)")
			prefer("animated-gif-lib-$gifLibVersion")
		}
	})
	jarJar(implementation("dev.latvian.mods:better-advanced-tooltips") {
		version {
			strictly("[$batVersion,)")
			prefer(batVersion)
		}
	})
}

// Make the game tests' KubeJS scripts available in the gametest run's game directory.
val copyGameTestScripts by tasks.registering(Copy::class) {
	mustRunAfter("prepareGametestRun")
	from(layout.projectDirectory.dir("src/test/kubejs"))
	into(layout.buildDirectory.dir("tmp/gametestRuns/gametest/kubejs"))
}

tasks.matching { it.name == "runGametest" }.configureEach {
	dependsOn(copyGameTestScripts)
}

// The test source set holds game tests (run via runGametest), not JUnit tests.
tasks.named<Test>("test") {
	failOnNoDiscoveredTests = false
}

publishing {
	repositories {
		val mavenUrl = System.getenv("MAVEN_URL") ?: return@repositories
		val mavenUsername = System.getenv("MAVEN_USERNAME") ?: return@repositories
		val mavenToken = System.getenv("MAVEN_TOKEN") ?: return@repositories

		maven {
			url = uri(mavenUrl)
			credentials {
				username = mavenUsername
				password = mavenToken
			}
		}
	}
}

/*unifiedPublishing {
    project {
        releaseType = "${ENV["RELEASE_TYPE"] ?: "release"}"
        gameVersions = Arrays.asList(rootProject.extra["supported_versions"].toString().split(", "))
        gameLoaders = listOf("neoforge")
        displayName = "$mod_name NeoForge ${project.version}"
        changelog = "https://kubejs.com/changelog?mc=${project.extra["minecraft_version"]}"
        mainPublication(tasks.jar)

        relations {
            depends {
                curseforge = "rhino"
                modrinth = "rhino"
            }
        }

        if (ENV["CURSEFORGE_KEY"] != null) {
            curseforge {
                token = ENV["CURSEFORGE_KEY"]
                id = project.extra["curseforge_id"].toString()
            }
        }

        if (ENV["MODRINTH_TOKEN"] != null) {
            modrinth {
                token = ENV["MODRINTH_TOKEN"]
                id = project.extra["modrinth_id"].toString()
                version = project.version.toString()
            }
        }
    }
}*/

configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
	module {
		if (!runningInCI) {
			isDownloadSources = true
			isDownloadJavadoc = true

			val subdirs = listOf(
				".architectury-transformer",
				"config",
				"crash-reports",
				"debug",
				"downloads",
				"dumps",
				"flashback",
				"local",
				"logs",
				"mods",
				"profilekeys",
				"saves",
				"voicechat_recordings",
				"command_history.txt",
				"options.txt",
				"server.properties",
				"world",
				"emi.json",
				"usercache.json",
				"usernamecache.json"
			)

			listOf("run", "runs/client", "runs/server").forEach { dir ->
				subdirs.forEach { ext -> excludeDirs.add(file("$dir/$ext")) }
			}
		}
	}
}
