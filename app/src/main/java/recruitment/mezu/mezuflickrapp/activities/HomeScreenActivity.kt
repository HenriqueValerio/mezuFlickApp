package recruitment.mezu.mezuflickrapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_home_screen.*
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.R
import recruitment.mezu.mezuflickrapp.model.User
import recruitment.mezu.mezuflickrapp.repositories.UserRepository
import recruitment.mezu.mezuflickrapp.viewmodel.UserViewModel

class HomeScreenActivity : AppCompatActivity() {

    private fun getViewModelFactory(repo: UserRepository) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserViewModel(this@HomeScreenActivity.application as MezuExerciseApp, repo) as T
        }
    }

    lateinit var app : MezuExerciseApp
    private lateinit var userViewModel : UserViewModel
    private var userId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_home_screen)

        userViewModel = ViewModelProviders
                .of(this, getViewModelFactory((application as MezuExerciseApp).repo.userRepository))
                .get(UserViewModel::class.java)

        start_button.setOnClickListener {
            val launchIntent = Intent(this, PictureListActivity::class.java)
            launchIntent.putExtra("userId", userId)
            startActivity(launchIntent)
        }

        app = application as MezuExerciseApp

        initUserObservers()

        userViewModel.getUser()

    }

    private fun initUserObservers(){
        // Request succeeded.
        userViewModel.user.observe(this, Observer<User> {
            Log.v(app.TAG,"Success on fetching user")
            userId = it.id
            start_button.visibility = View.VISIBLE
        })

        // Request failed.
        userViewModel.error.observe(this, Observer<String> {
            Log.v(app.TAG,"Error on fetching user")
        })
    }

}
