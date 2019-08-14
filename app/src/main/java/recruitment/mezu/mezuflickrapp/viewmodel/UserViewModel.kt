package recruitment.mezu.mezuflickrapp.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.model.User
import recruitment.mezu.mezuflickrapp.repositories.UserRepository


class UserViewModel(
        app: MezuExerciseApp,
        private val repo: UserRepository) : AndroidViewModel(app) {

    var user: MutableLiveData<User> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    val app = getApplication<MezuExerciseApp>()

    fun getUser() {
        repo.getUser(
                {
                    user.value = it
                },
                {
                    error.value = it
                    Log.e(app.TAG, it)
                }
        )
    }
}