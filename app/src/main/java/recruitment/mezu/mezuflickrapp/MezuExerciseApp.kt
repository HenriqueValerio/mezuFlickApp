package recruitment.mezu.mezuflickrapp

import android.app.Application
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import recruitment.mezu.mezuflickrapp.repositories.PicturesRepository
import recruitment.mezu.mezuflickrapp.repositories.Repository
import recruitment.mezu.mezuflickrapp.repositories.UserRepository

class MezuExerciseApp : Application(){
    val TAG : String = "MezuApp"
    lateinit var queue: RequestQueue
    lateinit var repo: Repository
    var page = 1

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)

        repo = Repository(
                PicturesRepository(this),
                UserRepository(this)
        )
    }
}