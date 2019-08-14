package recruitment.mezu.mezuflickrapp.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import kotlinx.android.synthetic.main.picture_details_layout.*
import kotlinx.android.synthetic.main.pictures_view.*
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.R
import recruitment.mezu.mezuflickrapp.runAsync

class PictureDetailsActivity : AppCompatActivity(){

    lateinit var app : MezuExerciseApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picture_details_layout)

        app = application as MezuExerciseApp

        pic_title.text = intent.getStringExtra("title")
        location.text = intent.getStringExtra("location")
        date.text = intent.getStringExtra("taken")
        val url = intent.getStringExtra("url")
        //runAsync {
            getImage(
                url,
                {
                    picture_large.setImageBitmap(it)
                    Log.i(app.TAG, "Fetching image on details")
                },
                {
                    Log.e(app.TAG, it)
                },
                app
            )
        //}


    }

    fun getImage(url: String, cbSuccess : ( (Bitmap)-> Unit ), cbError : ( (String)-> Unit ), app: MezuExerciseApp){
        val req = GetImage(url, Response.Listener{cbSuccess(it)}, Response.ErrorListener { cbError(it.toString())})
        app.queue.add(req)
    }

    class GetImage(url : String, listener : Response.Listener<Bitmap>, err : Response.ErrorListener) :
            ImageRequest(url,listener,0,0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,err) {
    }

}