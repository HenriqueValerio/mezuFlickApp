package recruitment.mezu.mezuflickrapp.activities

import android.os.Bundle
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

    lateinit var picturesViewModel: PicturesViewModel

    private fun getViewModelFactory(repo: PicturesRepository) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PicturesViewModel(this@PictureListActivity.application as MezuExerciseApp, repo) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_list)

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

        picturesViewModel.getPictures(intent.getStringExtra("userId"))
    }
}