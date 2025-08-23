package mn.univision.secretroom.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
data class ViewItem(
    val _id: String,
    val name: String,
    val kids: Boolean? = null,
    val __v: Int? = null,
    val title: ViewTitle? = null,
    val items: List<ViewSubItem>? = null
)

@Serializable
data class ViewTitle(
    val mn: String? = null,
    val en: String? = null
)

@Serializable
data class ViewSubItem(
    val _id: String,
    val uri: String? = null,
    val type: String? = null,
    val name: String? = null,
    val cache: Boolean? = null,
    val __v: Int? = null,
    val title: ViewTitle? = null,
    val items: List<ViewSubItem>? = emptyList(),  // Changed from Any to ViewSubItem
    val filter: ViewFilter? = null,
    val value: List<String>? = null,
    val actions: List<JsonElement>? = emptyList(),  // Using JsonElement for truly dynamic content
    val resources: List<ViewResource>? = null
)

@Serializable
data class ViewFilter(
    val exclude: List<JsonElement>? = emptyList(),  // Using JsonElement
    val include: List<JsonElement>? = emptyList()   // Using JsonElement
)

@Serializable
data class ViewResource(
    val uri: String? = null,
    val _id: String? = null
)