package mn.univision.secretroom.data.models

import com.google.gson.annotations.SerializedName

data class UnifiedListResponse(
    @SerializedName("metadata") val metadata: UnifiedListMetadata,
    @SerializedName("response") val response: List<UnifiedListItem>
)

data class UnifiedListMetadata(
    @SerializedName("request") val request: String,
    @SerializedName("fullLength") val fullLength: Int,
    @SerializedName("timestamp") val timestamp: Long
)

data class UnifiedListItem(
    @SerializedName("template") val template: String,
    @SerializedName("isSecured") val isSecured: Boolean,
    @SerializedName("attachments") val attachments: List<UnifiedAttachment>,
    @SerializedName("description") val description: String,
    @SerializedName("externalId") val externalId: String,
    @SerializedName("isBlockedBrowsing") val isBlockedBrowsing: Boolean,
    @SerializedName("parentId") val parentId: Int,
    @SerializedName("responseElementType") val responseElementType: String,
    @SerializedName("name") val name: String,
    @SerializedName("extrafields") val extrafields: List<UnifiedExtrafield>,
    @SerializedName("securityGroups") val securityGroups: List<Any>,
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: Int
)

data class UnifiedAttachment(
    @SerializedName("responseElementType") val responseElementType: String,
    @SerializedName("assetId") val assetId: String,
    @SerializedName("name") val name: String,
    @SerializedName("assetName") val assetName: String,
    @SerializedName("value") val value: String
)

data class UnifiedExtrafield(
    @SerializedName("responseElementType") val responseElementType: String,
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: String
)
