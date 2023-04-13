package coffee.cypher.json_processor

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonGrammar
import java.io.FilterReader
import java.io.Reader
import kotlin.properties.Delegates

class JsonProcessingFilter(input: Reader) : FilterReader(input) {
    private val original: JsonElement = jankson.loadElement(input.readText().replace("\r\n", "\n"))

    var outputFormat: JsonGrammar by Delegates.observable(JsonGrammar.STRICT) { _, _, _ -> retransform() }
    var pipeline: List<(JsonElement) -> JsonElement> by Delegates.observable(emptyList()) { _, _, _ -> retransform() }

    fun retransform() {
        `in` = pipeline
            .fold(original) { acc, f -> f(acc) }
            .toJson(outputFormat).reader()
    }

    companion object {
        private val jankson: Jankson = Jankson.builder().build()
    }
}