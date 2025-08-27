package mn.univision.secretroom.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mn.univision.secretroom.data.remote.DynamicContentApi
import mn.univision.secretroom.data.repositories.DynamicContentRepository
import mn.univision.secretroom.data.storage.DataStoreManager
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DynamicContentModule {

    @Provides
    @Singleton
    fun provideDynamicContentApi(retrofit: Retrofit): DynamicContentApi {
        return retrofit.create(DynamicContentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDynamicContentRepository(
        api: DynamicContentApi,
        dataStore: DataStoreManager
    ): DynamicContentRepository {
        return DynamicContentRepository(api, dataStore)
    }
}
