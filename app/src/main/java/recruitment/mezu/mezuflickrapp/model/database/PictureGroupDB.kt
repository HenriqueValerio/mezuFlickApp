package recruitment.mezu.mezuflickrapp.model.database
/*
import androidx.room.*
import recruitment.mezu.mezuflickrapp.model.PictureGroup

@Dao
interface PictureGroupDAO{
    @Query("SELECT * FROM pictureGroup")
    fun getAll(): List<PictureGroup>

    @Query("SELECT * FROM pictureGroup WHERE pictureGroupId = :id")
    fun findById(id: Int): PictureGroup

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( pictures: PictureGroup)
}

@Database(entities = arrayOf(PictureGroup::class), version = 1)
abstract class PictureGroupDataBase : RoomDatabase() {
    abstract fun pictureGroupDAO(): PictureGroupDAO
}*/