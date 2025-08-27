package mn.univision.secretroom.data.entities

import mn.univision.secretroom.data.models.UnifiedListItem

data class DynamicContent(
    val id: String,
    val name: String,
    val description: String,
    val posterHorizontal: String?,
    val posterVertical: String?,
    val videoUri: String? = null,
    val externalId: String,
    val deepLink: String? = null
)

fun UnifiedListItem.toDynamicContent(): DynamicContent {
    val horizontalBanner = attachments.find { it.name == "banner_horizontal" }?.value
    val verticalBanner = attachments.find { it.name == "banner_vertical" }?.value
    val deepLink = extrafields.find { it.name == "deep_Link" }?.value

    return DynamicContent(
        id = externalId,
        name = name,
        description = description,
        posterHorizontal = horizontalBanner?.let { "https://looktv.mn/RTEFacade/images$it" },
        posterVertical = verticalBanner?.let { "https://looktv.mn/RTEFacade/images$it" },
        externalId = externalId,
        deepLink = deepLink
    )
}
