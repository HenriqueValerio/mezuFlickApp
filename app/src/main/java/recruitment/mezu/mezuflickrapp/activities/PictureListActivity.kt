package recruitment.mezu.mezuflickrapp.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_picture_list.*
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.R
import recruitment.mezu.mezuflickrapp.adapters.PicturesAdapter
//import recruitment.mezu.mezuflickrapp.adapters.PicturesAdapter
import recruitment.mezu.mezuflickrapp.repositories.PicturesRepository
import recruitment.mezu.mezuflickrapp.viewmodel.PicturesViewModel

class PictureListActivity : AppCompatActivity(){

    lateinit var app : MezuExerciseApp
    lateinit var picturesViewModel: PicturesViewModel

    private fun getViewModelFactory(repo: PicturesRepository) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PicturesViewModel(this@PictureListActivity.application as MezuExerciseApp, repo) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_picture_list)

        app = application as MezuExerciseApp

        picture_list.setHasFixedSize(true)
        picture_list.layoutManager = LinearLayoutManager(this)

        picturesViewModel = ViewModelProviders
                .of(this, getViewModelFactory((application as MezuExerciseApp).repo.picturesRepository))
                .get(PicturesViewModel::class.java)

        picturesViewModel.pictures.observe(this, Observer {
            picture_list.adapter = PicturesAdapter(picturesViewModel, this, application as MezuExerciseApp)
        })

        picturesViewModel.error.observe(this, Observer {
            finish()
        })

        picturesViewModel.getPictures(intent.getStringExtra("userId"), app.page)

        previousPageBtn.setOnClickListener {
            if (!app.page.equals(1))
                app.page -=1
            picturesViewModel.getPictures(intent.getStringExtra("userId"), app.page)
        }

        nextPageBtn.setOnClickListener {
            app.page +=1
            picturesViewModel.getPictures(intent.getStringExtra("userId"), app.page)
        }

    }
}