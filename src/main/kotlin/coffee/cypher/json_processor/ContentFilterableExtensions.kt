package coffee.cypher.json_processor

import org.gradle.api.file.ContentFilterable

fun ContentFilterable.processJson(config: JsonFilterConfiguration.() -> Unit) {
    JsonFilterConfiguration(this).apply(config).doFilter()
}

