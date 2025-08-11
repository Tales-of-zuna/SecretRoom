

package mn.univision.secretroom.data.repositories

import mn.univision.secretroom.data.entities.toMovieCast
import mn.univision.secretroom.data.util.AssetsReader
import mn.univision.secretroom.data.util.StringConstants
import javax.inject.Inject

class MovieCastDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val movieCastDataReader = CachedDataReader {
        readMovieCastData(assetsReader, StringConstants.Assets.MovieCast).map {
            it.toMovieCast()
        }
    }

    suspend fun getMovieCastList() = movieCastDataReader.read()
}
