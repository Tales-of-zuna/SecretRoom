package mn.univision.secretroom

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import mn.univision.secretroom.data.repositories.MovieRepository
import mn.univision.secretroom.data.repositories.MovieRepositoryImpl

@HiltAndroidApp
class SecretRoomApplication : Application()


@InstallIn(SingletonComponent::class)
@Module
abstract class MovieRepositoryModule {

    @Binds
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}
