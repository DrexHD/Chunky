plugins {
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
}

val shade: Configuration by configurations.creating

repositories {
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = "1.21.1")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.21.1+build.3", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.16.5")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.104.0+1.21.1")
    modImplementation (group = "maven.modrinth", name = "moonrise-opt", version = "0.1.0-beta.3+23eddfe")
    modCompileOnly(group = "me.lucko", name = "fabric-permissions-api", version = "0.3.1")
    implementation(project(":chunky-common"))
    shade(project(":chunky-common"))
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                "id" to rootProject.name,
                "version" to project.version,
                "name" to project.property("artifactName"),
                "description" to project.property("description"),
                "author" to project.property("author"),
                "github" to project.property("github")
            )
        }
    }
    shadowJar {
        configurations = listOf(shade)
        archiveClassifier.set("dev")
        archiveFileName.set(null as String?)
    }
    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("${project.property("artifactName")}-${project.version}.jar")
    }
}
