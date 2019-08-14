package recruitment.mezu.mezuflickrapp.model

import androidx.room.Entity

@Entity(tableName = "pictures",
        primaryKeys = ["id"])
class Picture(
        val id : String,
        val title : String,
        val taken : String?,
        val location : String?,
        val url : String?,
        val url_thumbnail : String?
)
