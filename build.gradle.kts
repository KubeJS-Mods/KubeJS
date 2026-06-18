import com.almostreliable.almostgradle.dependency.LoadingMode
import java.util.Locale
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
	id("net.neoforged.moddev") version "2.0.138"
	id("com.almostreliable.almostgradle") version "2.2.0"
	id("idea")
	jacoco
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
		testMod = true
		gameTests = true
		testFramework = true
		jUnit = true
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

// Code coverage. JaCoCo's agent is attached to both test JVMs and scoped to KubeJS' own classes;
// `coverageReport` merges the two execution-data files into one report.
val coverageIncludes = listOf("dev.latvian.mods.kubejs.*")

jacoco {
	toolVersion = "0.8.15" // 0.8.14+ supports Java 25 class files
	// Gradle's JaCoCo plugin only instruments `Test` tasks by default; opt the gametest JavaExec in.
	applyTo(tasks.withType<JavaExec>().matching { it.name == "runGametest" })
}

// When a coverage task is requested, force a fresh run of both test JVMs so the report always
// reflects a real execution (otherwise Gradle's up-to-date check skips them and reuses old data).
val coverageRequested = gradle.startParameter.taskNames.any {
	it.substringAfterLast(':').startsWith("coverage")
}

// The test source set holds both JUnit unit tests (...unittest, run by `test`) and the game-test
// mod (...testmod, run by `runGametest`). Allow `test` to pass before any unit tests are present.
tasks.named<Test>("test") {
	failOnNoDiscoveredTests = false
	if (coverageRequested) {
		outputs.upToDateWhen { false }
	}
	extensions.configure<JacocoTaskExtension> {
		includes = coverageIncludes
	}
}

tasks.withType<JavaExec>().matching { it.name == "runGametest" }.configureEach {
	// When both run (the coverage flow), order the cheap unit tests first so the heavy game-test
	// server never shares a heap window with the JUnit fork, even under --parallel.
	mustRunAfter("test")
	if (coverageRequested) {
		outputs.upToDateWhen { false }
	}
	extensions.configure<JacocoTaskExtension> {
		isEnabled = true
		includes = coverageIncludes
	}
}

val coverageReport by tasks.registering(JacocoReport::class) {
	group = "verification"
	description = "Merges coverage from the JUnit (test) and game-test (runGametest) JVMs."
	dependsOn("test", "runGametest")
	sourceSets(sourceSets["main"])
	executionData(fileTree(layout.buildDirectory) {
		include("jacoco/test.exec", "jacoco/runGametest.exec")
	})
	reports {
		html.required = true
		csv.required = true
		xml.required = false
		html.outputLocation = layout.buildDirectory.dir("reports/coverage/html")
		csv.outputLocation = layout.buildDirectory.file("reports/coverage/coverage.csv")
	}
}

// Derive a small JSON summary (overall instruction/branch/line %) from the JaCoCo CSV, for CI to
// read when posting the coverage PR comment. JaCoCo has no native JSON report.
val coverageSummaryJson by tasks.registering {
	group = "verification"
	description = "Writes coverage-summary.json from the JaCoCo CSV report."
	dependsOn(coverageReport)
	val csvFile = layout.buildDirectory.file("reports/coverage/coverage.csv")
	val jsonFile = layout.buildDirectory.file("reports/coverage/coverage-summary.json")
	inputs.file(csvFile)
	outputs.file(jsonFile)
	doLast {
		val rows = csvFile.get().asFile.readLines().drop(1).filter { it.isNotBlank() }
		var instrMissed = 0L; var instrCovered = 0L
		var branchMissed = 0L; var branchCovered = 0L
		var lineMissed = 0L; var lineCovered = 0L
		for (row in rows) {
			val c = row.split(',')
			if (c.size < 9) continue
			instrMissed += c[3].toLong(); instrCovered += c[4].toLong()
			branchMissed += c[5].toLong(); branchCovered += c[6].toLong()
			lineMissed += c[7].toLong(); lineCovered += c[8].toLong()
		}
		fun pct(covered: Long, missed: Long): String {
			val total = covered + missed
			val v = if (total == 0L) 0.0 else covered * 100.0 / total
			return String.format(Locale.ROOT, "%.2f", v)
		}
		fun metric(name: String, covered: Long, missed: Long) =
			"""    "$name": { "covered": $covered, "missed": $missed, "percent": ${pct(covered, missed)} }"""
		val json = buildString {
			appendLine("{")
			appendLine(metric("instruction", instrCovered, instrMissed) + ",")
			appendLine(metric("branch", branchCovered, branchMissed) + ",")
			appendLine(metric("line", lineCovered, lineMissed))
			appendLine("}")
		}
		jsonFile.get().asFile.writeText(json)
		logger.lifecycle("Coverage (instructions): ${pct(instrCovered, instrMissed)}% -> ${jsonFile.get().asFile}")
	}
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
