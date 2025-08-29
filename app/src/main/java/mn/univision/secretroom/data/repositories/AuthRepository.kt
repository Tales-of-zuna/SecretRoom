package mn.univision.secretroom.data.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.flow.first
import mn.univision.secretroom.data.remote.AuthApiService
import mn.univision.secretroom.data.storage.DataStoreManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val dataStore: DataStoreManager,
    private val context: Context
) {

    companion object {
        private const val TAG = "AuthRepository"
        private val CONTENT_URI = "content://com.nes.serialprovider.DataShareProvider".toUri()
        private const val ETHER_MAC = "ethernetMac"
        private const val SERIAL_NUMBER = "serialnumber"
    }

    sealed class AuthResult {
        object Loading : AuthResult()
        object Success : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun performAuthentication(): AuthResult {
        return try {
            val macAddress = getEthernetMac()
            val serialNumber = getSerialNumber()

            if (macAddress.isNullOrEmpty() || serialNumber.isNullOrEmpty()) {
                return AuthResult.Error("Төхөөрөмжийн мэдээлэл авах боломжгүй байна")
            }

            dataStore.saveMacAddress(macAddress)
            dataStore.saveSerialNumber(serialNumber)

            Log.d(TAG, "MAC Address: $macAddress, Serial Number: $serialNumber")

            val loginResponse = api.login(
                macAddress = macAddress,
                serialNumber = serialNumber
//                macAddress = "D4:CF:F9:20:D0:06", serialNumber = "QA99999986111829"
            )

            if (!loginResponse.isSuccessful) {
                return AuthResult.Error("Login failed: ${loginResponse.message()}")
            }

            val loginBody = loginResponse.body()
            if (loginBody?.response?.status != "SUCCESS") {
                return AuthResult.Error("Login status failed: ${loginBody?.response?.message}")
            }
            val cookieValue = loginBody.response.message
            dataStore.saveCookie(cookieValue)

            val householdResponse = api.getHousehold(cookie = cookieValue)

            if (!householdResponse.isSuccessful) {
                return AuthResult.Error("Failed to get household: ${householdResponse.message()}")
            }

            val householdBody = householdResponse.body()
            val householdId = householdBody?.response?.id?.toString()

            if (householdId != null) {
                dataStore.saveHouseholdId(householdId)
                AuthResult.Success
            } else {
                AuthResult.Error("Failed to get household ID")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Authentication error", e)
            AuthResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun getEthernetMac(): String? {
        return try {
            val cursor: Cursor? = context.contentResolver.query(
                CONTENT_URI, null, null, null, null
            )
            cursor?.use {
                val colIndex = it.getColumnIndex(ETHER_MAC)
                if (it.moveToFirst() && colIndex >= 0) {
                    it.getString(colIndex)
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting MAC address", e)
            null
        }
    }

    private fun getSerialNumber(): String? {
        return try {
            val cursor: Cursor? = context.contentResolver.query(
                CONTENT_URI, null, null, null, null
            )
            cursor?.use {
                val colIndex = it.getColumnIndex(SERIAL_NUMBER)
                if (it.moveToFirst() && colIndex >= 0) {
                    it.getString(colIndex)
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting serial number", e)
            null
        }
    }

    suspend fun isAuthenticated(): Boolean {
        val cookie = dataStore.cookieFlow.first()
        val householdId = dataStore.householdIdFlow.first()
        return !cookie.isNullOrEmpty() && !householdId.isNullOrEmpty()
    }

    suspend fun getSavedHouseholdId(): String? {
        return dataStore.householdIdFlow.first()
    }

    suspend fun getSavedCookie(): String? {
        return dataStore.cookieFlow.first()
    }

    suspend fun logout() {
        dataStore.clearAuthData()
    }
}