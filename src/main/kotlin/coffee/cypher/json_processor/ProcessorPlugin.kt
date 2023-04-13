package coffee.cypher.json_processor

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        GroovyInterop.setup()
    }
}
