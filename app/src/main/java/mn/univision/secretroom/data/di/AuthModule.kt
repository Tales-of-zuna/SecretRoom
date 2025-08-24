package mn.univision.secretroom.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mn.univision.secretroom.data.remote.AuthApiService
import mn.univision.secretroom.data.repositories.AuthRepository
import mn.univision.secretroom.data.storage.DataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApiService,
        dataStore: DataStoreManager,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepository(api, dataStore, context)
    }

}