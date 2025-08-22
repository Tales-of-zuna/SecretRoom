package mn.univision.secretroom.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("secret_room_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_HOUSEHOLD = stringPreferencesKey("household_id")
        private val KEY_COOKIE = stringPreferencesKey("auth_cookie")
        private val KEY_MAC_ADDRESS = stringPreferencesKey("mac_address")
        private val KEY_SERIAL_NUMBER = stringPreferencesKey("serial_number")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val householdIdFlow: Flow<String?> = context.dataStore.data.map { it[KEY_HOUSEHOLD] }
    val cookieFlow: Flow<String?> = context.dataStore.data.map { it[KEY_COOKIE] }
    val macAddressFlow: Flow<String?> = context.dataStore.data.map { it[KEY_MAC_ADDRESS] }
    val serialNumberFlow: Flow<String?> = context.dataStore.data.map { it[KEY_SERIAL_NUMBER] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[KEY_TOKEN] = token }
    }

    suspend fun saveHouseholdId(id: String) {
        context.dataStore.edit { prefs -> prefs[KEY_HOUSEHOLD] = id }
    }


    suspend fun saveCookie(cookie: String) {
        context.dataStore.edit { prefs -> prefs[KEY_COOKIE] = cookie }
    }

    suspend fun saveMacAddress(macAddress: String) {
        context.dataStore.edit { prefs -> prefs[KEY_MAC_ADDRESS] = macAddress }
    }

    suspend fun saveSerialNumber(serialNumber: String) {
        context.dataStore.edit { prefs -> prefs[KEY_SERIAL_NUMBER] = serialNumber }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_HOUSEHOLD)
            prefs.remove(KEY_COOKIE)
        }
    }
}