package mn.univision.secretroom.data.models

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ViewItem(
    @SerializedName("_id") val _id: String,
    @SerializedName("name") val name: String,
    @SerializedName("kids") val kids: Boolean? = null,
    @SerializedName("__v") val __v: Int? = null,
    @SerializedName("title") val title: ViewTitle? = null,
    @SerializedName("items") val items: List<ViewSubItem>? = null
)

data class ViewTitle(
    @SerializedName("mn") val mn: String? = null,
    @SerializedName("en") val en: String? = null
)

data class ViewSubItem(
    @SerializedName("_id") val _id: String,
    @SerializedName("uri") val uri: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("cache") val cache: Boolean? = null,
    @SerializedName("__v") val __v: Int? = null,
    @SerializedName("title") val title: ViewTitle? = null,
    @SerializedName("items") val items: List<ViewSubItem>? = emptyList(),
    @SerializedName("filter") val filter: ViewFilter? = null,
    @SerializedName("value") val value: JsonElement? = null,
    @SerializedName("actions") val actions: List<JsonElement>? = emptyList(),
    @SerializedName("resources") val resources: List<ViewResource>? = null
) {
    fun getValueAsList(): List<String> {
        return when {
            value == null -> emptyList()
            value.isJsonArray -> {
                value.asJsonArray.mapNotNull {
                    if (it.isJsonPrimitive) it.asString else null
                }
            }

            value.isJsonPrimitive -> listOf(value.asString)
            else -> emptyList()
        }
    }
}

data class ViewFilter(
    @SerializedName("exclude") val exclude: List<JsonElement>? = emptyList(),
    @SerializedName("include") val include: List<JsonElement>? = emptyList()
)

data class ViewResource(
    @SerializedName("uri") val uri: String? = null,
    @SerializedName("_id") val _id: String? = null
)