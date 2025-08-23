package mn.univision.secretroom.data.remote

import mn.univision.secretroom.data.models.ViewItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface AuthApiService {
    @GET("RTEFacade/Login")
    suspend fun login(
        @Query("client") client: String = "json",
        @Query("mac_address") macAddress: String,
        @Query("serial_number") serialNumber: String
    ): Response<LoginResponse>

    @GET
    suspend fun getViews(
        @Url url: String = "https://looktv.mn/appmgr/views.json",
        @Header("Cookie") cookie: String? = null
    ): Response<List<ViewItem>>


    @GET("RTEFacade/GetHousehold")
    suspend fun getHousehold(
        @Query("client") client: String = "json",
        @Header("Cookie") cookie: String
    ): Response<HouseholdResponse>
}

data class LoginResponse(
    val metadata: LoginMetadata,
    val response: LoginResponseData
)

data class LoginMetadata(
    val request: String,
    val timestamp: Long
)

data class LoginResponseData(
    val id: String,
    val message: String,
    val status: String
)

data class HouseholdResponse(
    val metadata: HouseholdMetadata,
    val response: HouseholdData
)

data class HouseholdMetadata(
    val request: String,
    val timestamp: Long
)

data class HouseholdData(
    val id: Int,
    val externalId: String,
    val username: String,
    val phone: String,
    val timezone: String,
    val languageCode: String,
    val billingMethod: String,
    val maxTerminals: Int,
    val groups: List<SecurityGroup>,
    val extrafields: List<Extrafield>,
    val parentalPin: String? = null,
    val type: Int? = null,
    val discount: Double? = null
)

data class SecurityGroup(
    val responseElementType: String,
    val externalId: String,
    val type: String
)

data class Extrafield(
    val responseElementType: String,
    val name: String,
    val value: String
)