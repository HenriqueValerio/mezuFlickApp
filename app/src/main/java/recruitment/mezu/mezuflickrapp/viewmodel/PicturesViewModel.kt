package recruitment.mezu.mezuflickrapp.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.model.Picture
import recruitment.mezu.mezuflickrapp.repositories.PicturesRepository

class PicturesViewModel(
        app : MezuExerciseApp,
        private val repository: PicturesRepository) : AndroidViewModel (app){

    var pictures: MutableLiveData<List<Picture>> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    val app = getApplication<MezuExerciseApp>()

    fun getPictures(userId : String, page : Int){
        if (!userId.isBlank()){
            repository.getPictures({
                    pictures.value = it
                    Log.i(app.TAG, "Got pictures ready")
                },
                {
                    error.value = it
                    Log.e(app.TAG, it.plus("\t\tsomething went wrong when getting pictures from API"))
                },
                    userId,
                    page)
        }
    }
}