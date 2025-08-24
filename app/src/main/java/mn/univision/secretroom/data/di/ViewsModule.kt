package mn.univision.secretroom.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mn.univision.secretroom.data.storage.DataStoreManager
import mn.univision.secretroom.data.storage.ViewsDataManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewsModule {

    @Provides
    @Singleton
    fun provideViewsDataManager(
        @ApplicationContext context: Context,
        dataStore: DataStoreManager
    ): ViewsDataManager {
        return ViewsDataManager(context, dataStore)
    }
}
