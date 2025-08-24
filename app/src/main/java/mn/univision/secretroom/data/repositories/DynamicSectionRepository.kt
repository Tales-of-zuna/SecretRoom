package mn.univision.secretroom.data.repositories

import android.content.Context
import mn.univision.secretroom.data.remote.AuthApiService
import mn.univision.secretroom.data.storage.DataStoreManager
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DynamicSectionRepository @Inject constructor(
    private val api: AuthApiService,
    private val dataStore: DataStoreManager,
    private val context: Context
) 