package coffee.cypher.json_processor

import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonGrammar
import org.gradle.api.file.ContentFilterable

class JsonFilterConfiguration(private val spec: ContentFilterable) {
    var outputFormat = JsonGrammar.STRICT
    private val pipeline = mutableListOf<(JsonElement) -> JsonElement>()

    fun pipeline(config: JsonPipelineScope.() -> Unit) {
        pipeline += JsonPipelineScope().apply(config).collect()
    }

    internal fun doFilter() {
        spec.filter(
            mapOf(
                "outputFormat" to outputFormat,
                "pipeline" to pipeline
            ),
            JsonProcessingFilter::class.java
        )
    }
}

fun JsonFilterConfiguration.outputJson5() {
    outputFormat = JsonGrammar.JSON5
}