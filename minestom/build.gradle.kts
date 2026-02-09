plugins {
	id("com.gradleup.shadow") version "9.3.0"
	kotlin("jvm") version "2.3.0"
	application
	java
	`maven-publish`
}

group = "com.github.hapily04"
version = "1.0.0-alpha.1"

repositories {
    mavenCentral()
	maven("https://jitpack.io")
	maven("https://repo.hypera.dev/snapshots/")
	maven("https://repo.lucko.me/")
	maven("https://maven.hapily.me/releases")
	maven("https://maven.hapily.me/snapshots")
	maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
	implementation("net.minestom:minestom:2026.01.08-1.21.11")
	implementation("net.kyori:adventure-text-minimessage:4.26.1")
	implementation("dev.hollowcube:polar:1.15.0")
	implementation("it.unimi.dsi:fastutil:8.5.18") // fix polar error
	implementation("dev.lu15:luckperms-minestom:5.5-SNAPSHOT")
	implementation("com.h2database:h2:2.2.224") // fix luckperms cause it's lowkey being  stupid
	//implementation("dev.lu15:spark-minestom:1.10-SNAPSHOT")
	implementation("org.spongepowered:configurate-hocon:3.7.2") // configuration using hocon
	implementation("org.jline:jline:3.28.0") // part of terminal implementation
	compileOnly("org.jetbrains:annotations:26.0.2")
	implementation(group = "org.eclipse.jdt", name = "org.eclipse.jdt.annotation", version = "2.2.700")
	implementation("org.slf4j:slf4j-simple:2.0.16")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation(project(":common"))
}

kotlin {
    jvmToolchain(25)
}

application {
	mainClass = "com.github.hapily04.skriptminestom.SkriptMinestom"
}

java {
	withSourcesJar()
}

publishing {
	repositories {
		maven {
			url = uri("https://maven.hapily.me/snapshots")
			credentials {
				username = (project.findProperty("repoHapilyUsername") as? String)
					?: throw GradleException("Missing global property 'repoHapilyUsername'")
				password = (project.findProperty("repoHapilyPassword") as? String)
					?: throw GradleException("Missing global property 'repoHapilyPassword'")
			}
		}
	}
}