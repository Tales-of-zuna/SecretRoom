package mn.univision.secretroom.data.repository

import kotlinx.coroutines.flow.first
import mn.univision.secretroom.data.remote.AuthApiService
import mn.univision.secretroom.data.storage.DataStoreManager
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val dataStore: DataStoreManager
) {
    suspend fun fetchAndSaveAuthData() {
        // 1. Fetch Token
        val tokenResponse = api.getToken(
            mapOf("client_id" to "xxx", "client_secret" to "yyy") // replace with real values
        )
        val token = tokenResponse.accessToken

        // 2. Fetch Household
        val householdResponse = api.getHouseholdId("Bearer $token")

        // 3. Save both into datastore
        dataStore.saveToken(token)
        dataStore.saveHouseholdId(householdResponse.householdId)
    }

    suspend fun getSavedHouseholdId(): String? {
        return dataStore.householdIdFlow.first()
    }
}
