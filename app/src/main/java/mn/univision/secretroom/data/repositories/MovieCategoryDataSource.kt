

package mn.univision.secretroom.data.repositories

import mn.univision.secretroom.data.entities.toMovieCategory
import mn.univision.secretroom.data.util.AssetsReader
import mn.univision.secretroom.data.util.StringConstants
import javax.inject.Inject

class MovieCategoryDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val movieCategoryDataReader = CachedDataReader {
        readMovieCategoryData(assetsReader, StringConstants.Assets.MovieCategories).map {
            it.toMovieCategory()
        }
    }

    suspend fun getMovieCategoryList() = movieCategoryDataReader.read()
}
