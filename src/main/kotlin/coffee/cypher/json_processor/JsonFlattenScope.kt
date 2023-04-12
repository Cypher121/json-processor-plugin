package coffee.cypher.json_filters

import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonObject

class JsonFlattenScope {
    var keyDelimiter = ""
    private val skipJoinerAfter = mutableSetOf<String>()
    var delimitEmptyKeys = false

    fun skipDelimiterAfter(vararg suffixes: String) {
        skipJoinerAfter += suffixes
    }

    internal fun createFlattener(): (JsonElement) -> JsonElement {
        fun flatten(element: JsonElement): JsonElement = if (element is JsonObject) {
            val newObj = JsonObject()
            newObj.marshaller = element.marshaller

            newObj.putAll(
                element.flatMap { (k, v) ->
                    val newValue = flatten(v)
                    val newPrefix = when {
                        !delimitEmptyKeys && k.isEmpty() -> k
                        skipJoinerAfter.any { k.endsWith(it) } -> k
                        else -> k + keyDelimiter
                    }

                    if (newValue is JsonObject)
                        newValue.entries.map { (key, value) ->
                            val newKey = if (!delimitEmptyKeys && key.isEmpty()) {
                                k
                            } else {
                                "$newPrefix$key"
                            }
                            newKey to value
                        }
                    else
                        listOf(k to newValue)
                })

            newObj
        } else {
            element
        }

        return ::flatten
    }
}