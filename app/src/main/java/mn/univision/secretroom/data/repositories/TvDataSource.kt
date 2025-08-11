

package mn.univision.secretroom.data.repositories

import mn.univision.secretroom.data.entities.ThumbnailType
import mn.univision.secretroom.data.entities.toMovie
import mn.univision.secretroom.data.util.AssetsReader
import mn.univision.secretroom.data.util.StringConstants
import javax.inject.Inject

class TvDataSource @Inject constructor(
    assetsReader: AssetsReader
) {
    private val mostPopularTvShowsReader = CachedDataReader {
        readMovieData(assetsReader, StringConstants.Assets.MostPopularTVShows)
    }

    suspend fun getTvShowList() = mostPopularTvShowsReader.read().subList(0, 5).map {
        it.toMovie(ThumbnailType.Long)
    }

    suspend fun getBingeWatchDramaList() = mostPopularTvShowsReader.read().subList(6, 15).map {
        it.toMovie()
    }
}
