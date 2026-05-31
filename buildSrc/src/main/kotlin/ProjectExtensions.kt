import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

fun Project.localProperty(key: String): String {
    val file = rootProject.file("local.properties")
    if (!file.exists()) error("local.properties not found at ${file.absolutePath}")

    val properties = Properties()
    properties.load(FileInputStream(file))
    return properties.getProperty(key)
        ?: error("$key not found in local.properties")
}
