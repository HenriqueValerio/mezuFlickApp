package recruitment.mezu.mezuflickrapp.model.database


import androidx.room.*
import recruitment.mezu.mezuflickrapp.model.Picture

@Dao
interface PicturesDAO{
    @Query("SELECT * FROM pictures")
    fun getAll(): List<Picture>

    @Query("SELECT * FROM pictures WHERE id = :id")
    fun findById(id: String): Picture

    @Query("SELECT * FROM pictures WHERE page = :page")
    fun findByPage(page: Int): List<Picture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( picture: Picture)
}

@Database(entities = arrayOf(Picture::class), version = 1)
abstract class PicturesDataBase : RoomDatabase() {
    abstract fun pictureDAO(): PicturesDAO
}