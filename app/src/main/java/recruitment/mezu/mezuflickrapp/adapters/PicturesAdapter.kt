package recruitment.mezu.mezuflickrapp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.R
import recruitment.mezu.mezuflickrapp.activities.PictureDetailsActivity
import recruitment.mezu.mezuflickrapp.model.Picture
import recruitment.mezu.mezuflickrapp.viewmodel.PicturesViewModel


class PicturesViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {

    private val picturesView: TextView = view.findViewById(R.id.picture_title)

    fun bindTo(pictures: Picture?) {
        picturesView.text = pictures?.title
    }
}

class PicturesAdapter(val viewModel: PicturesViewModel, val context: Context, val app : MezuExerciseApp) : RecyclerView.Adapter<PicturesViewHolder>(){

    override fun getItemCount(): Int = viewModel.pictures.value?.size?:0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder =
            PicturesViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.pictures_view, parent, false) as ViewGroup
            )

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bindTo(viewModel.pictures.value?.get(position))

        holder.itemView.setOnClickListener{
            val i = Intent(context, PictureDetailsActivity::class.java)
            i.putExtra("title", viewModel.pictures.value?.get(position)?.title)
            i.putExtra("taken", viewModel.pictures.value?.get(position)?.taken)
            i.putExtra("location", viewModel.pictures.value?.get(position)?.location)
            i.putExtra("url", viewModel.pictures.value?.get(position)?.url)
            context.startActivity(i)
        }

        val url = viewModel.pictures.value?.get(position)?.url_thumbnail
        getImage(
                url!!,
                {
                    holder.itemView.findViewById<ImageView>(R.id.picture_image).setImageBitmap(it)
                    Log.i(app.TAG, "Fetching image of position $position")
                },
                {
                    Log.e(app.TAG, it)
                },
                app
        )
    }

    fun getImage(url: String, cbSuccess : ( (Bitmap)-> Unit ), cbError : ( (String)-> Unit ), app: MezuExerciseApp ){
        val req = GetImage(url, Response.Listener{cbSuccess(it)}, Response.ErrorListener { cbError(it.toString())})
        app.queue.add(req)
    }

    class GetImage(url : String, listener : Response.Listener<Bitmap>, err : Response.ErrorListener) :
            ImageRequest(url,listener,0,0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,err) {
    }

}