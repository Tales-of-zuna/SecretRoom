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
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val householdIdFlow: Flow<String?> = context.dataStore.data.map { it[KEY_HOUSEHOLD] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[KEY_TOKEN] = token }
    }

    suspend fun saveHouseholdId(id: String) {
        context.dataStore.edit { prefs -> prefs[KEY_HOUSEHOLD] = id }
    }
}
