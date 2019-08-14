package recruitment.mezu.mezuflickrapp.repositories

import android.util.Log
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import recruitment.mezu.mezuflickrapp.AsyncWork
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.model.Picture
//import recruitment.mezu.mezuflickrapp.model.PictureGroup
import recruitment.mezu.mezuflickrapp.model.PictureInfo
import recruitment.mezu.mezuflickrapp.model.PictureURL
//import recruitment.mezu.mezuflickrapp.model.database.PictureGroupDataBase
import recruitment.mezu.mezuflickrapp.model.database.PicturesDataBase
import recruitment.mezu.mezuflickrapp.runAsync
import com.android.volley.toolbox.RequestFuture




class PicturesRepository(private val app : MezuExerciseApp){

    private val ENDPOINT_URL = "https://www.flickr.com/services/rest/?method="
    private val picturesPerPage : Int = 25
    private var page = app.page
    private val url_getPublicPhotos = ENDPOINT_URL.plus("flickr.people.getPublicPhotos&api_key=76479578b1c3e371d8c0f649acd42647&per_page=".plus(picturesPerPage).plus("&format=json&nojsoncallback=1&user_id="))
    private val url_getInfo = ENDPOINT_URL.plus("flickr.photos.getInfo&api_key=76479578b1c3e371d8c0f649acd42647&format=json&nojsoncallback=1&photo_id=")
    private val url_getSizes = ENDPOINT_URL.plus("flickr.photos.getSizes&api_key=76479578b1c3e371d8c0f649acd42647&format=json&nojsoncallback=1&photo_id=")

    var pictures : ArrayList<Picture> = ArrayList()

    private val picturesDB = Room
            .databaseBuilder(app, PicturesDataBase::class.java, "pictures-db")
            .build()

    /*private val pictureGroupDB = Room
            .databaseBuilder(app, PictureGroupDataBase::class.java, "pictureGroup-db")
            .build()*/

    private fun saveToPicturesDB(picture: Picture): AsyncWork<Picture> {
        return runAsync{
            Log.v(app.TAG, "Saving elements to DB")
            picturesDB.pictureDAO().insertAll(picture)
            picture
        }
    }

    /*private fun saveToPictureGroupDB(pictureGroup: PictureGroup): AsyncWork<PictureGroup> {
        return runAsync{
            Log.v(app.TAG, "Saving pic group to DB")
            pictureGroupDB.pictureGroupDAO().insertAll(pictureGroup)
            pictureGroup
        }
    }*/

    fun getPictures(successCb: (List<Picture>?) -> Unit, failCb: (String) -> Unit, userId : String, page : Int) {
        //getPublicPhotos(user_id) -> id, title
        //getInto(photo_id) -> taken, location, url
        //getSizes(photo_id) -> url_thumbnail

        pictures.clear()

        runAsync {
            //pictureGroupDB.pictureGroupDAO().findById(app.page)
            picturesDB.pictureDAO().findByPage(page)
        }.andThen{
            if (it == null || it.isEmpty()){
                runAsync {
                    if (picturesDB.pictureDAO().getAll().isEmpty())
                        getPublicPhotos(successCb,failCb, userId, page)
                    else
                        getPublicPhotosSync(successCb,failCb, userId, page)
                }
            }
            else {
                Log.v(app.TAG, "Got pics from DB")
                successCb(it)
            }
        }
    }

    private fun getPublicPhotosSync(successCb: (List<Picture>?) -> Unit, failCb: (String) -> Unit, userId: String, page: Int) {
        val future = RequestFuture.newFuture<JSONObject>()
        val request = JsonObjectRequest(url_getPublicPhotos.plus(userId).plus("&page=").plus(page), JSONObject(), future, future)
        app.queue.add(request)

        try {
            val response = future.get() // this will block
            processResponse(response, successCb, failCb, page, pictures)
        } catch (e: Exception) {
            // exception handling
        }
    }


    private fun getPublicPhotos(successCb: (List<Picture>?) -> Unit, failCb: (String) -> Unit,userId: String, page : Int){



        val getPublicPhotosRequest = JsonObjectRequest(Request.Method.GET, url_getPublicPhotos.plus(userId).plus("&page=").plus(page), null,
                Response.Listener { response ->
                    processResponse(response, successCb, failCb, page, pictures)
                },
                Response.ErrorListener { error ->
                    failCb(error.toString())
                }
        )

        app.queue.add(getPublicPhotosRequest)
    }

    private fun processResponse(response: JSONObject?, successCb: (List<Picture>?) -> Unit, failCb: (String) -> Unit, page: Int, pictures : ArrayList<Picture>) {
        val jsonobj : JSONObject = response!!.getJSONObject("photos")
        val photos : JSONArray = jsonobj.getJSONArray("photo")
        var completed = 0

        for (i in 0 until photos.length()) {
            val loc = photos.get(i) as JSONObject
            var operationsCompleted = 0

            val id: String = loc.getString("id")
            val title: String = loc.getString("title")
            var taken: String? = ""
            var location: String? = ""
            var url: String? = ""
            var urlThumbnail: String? = ""


            runAsync {
                val dbPicture: Picture = picturesDB.pictureDAO().findById(id)
                if (dbPicture != null) {
                    pictures.add(dbPicture)
                    completed += 1
                    if (completed.equals(picturesPerPage))
                        successCb(pictures)
                } else {
                    getInfo({
                        taken = it?.taken
                        location = it?.location
                        operationsCompleted += 1

                        if (operationsCompleted.equals(2)) {
                            val pic = Picture(id, title, taken, location, url, urlThumbnail, page)
                            saveToPicturesDB(pic).andThen {
                                pictures.add(pic)
                                completed += 1
                                if (completed.equals(picturesPerPage)){
                                    successCb(pictures)
                                    //saveToPictureGroupDB(PictureGroup(page,pictures))
                                }
                            }
                        }

                    }, {
                        Log.v(app.TAG, "Error")
                    }, id)

                    getURL({
                        url = it?.url
                        urlThumbnail = it?.url_thumbnail
                        operationsCompleted += 1

                        if (operationsCompleted.equals(2)) {
                            val pic = Picture(id, title, taken, location, url, urlThumbnail, page)
                            saveToPicturesDB(pic).andThen {
                                pictures.add(pic)
                                completed += 1
                                if (completed.equals(picturesPerPage)){
                                    successCb(pictures)
                                    //saveToPictureGroupDB(PictureGroup(page,pictures))
                                }
                            }
                        }

                    }, {
                        Log.v(app.TAG, "Error")
                    }, id)

                }
            }
        }
    }

    private fun getInfo(successCb: (PictureInfo?) -> Unit, failCb: (String) -> Unit, photoId : String){

        val getPictureInfoRequest = JsonObjectRequest(Request.Method.GET, url_getInfo.plus(photoId), null,
                Response.Listener { response ->
                    val jsonobj : JSONObject = response.getJSONObject("photo")
                    val dates : JSONObject = jsonobj.getJSONObject("dates")
                    val taken : String = dates.getString("taken")
                    val locationValue : String
                    if (jsonobj.has("location")){
                        val location : JSONObject = jsonobj.getJSONObject("location")
                        val location_countryObj : JSONObject = location.getJSONObject("country")
                        val location_country : String = location_countryObj.getString("_content")

                        val location_region : String
                        if (location.has("region")){
                            val location_regionOnj : JSONObject = location.getJSONObject("region")
                            location_region = location_regionOnj.getString("_content")
                        }
                        else
                            location_region = "-"

                        val location_locality : String
                        if (location.has("locality")){
                            val location_localityObj : JSONObject = location.getJSONObject("locality")
                            location_locality = location_localityObj.getString("_content")
                        }
                        else
                            location_locality = "-"

                        locationValue = location_country.plus(", ").plus(location_region).plus(", ").plus(location_locality)
                    }
                    else
                        locationValue = "No location provided"
                    successCb(PictureInfo(taken,locationValue))
                },
                Response.ErrorListener { error ->
                    failCb(error.toString())
                }
        )

        app.queue.add(getPictureInfoRequest)

    }

    private fun getURL(successCb: (PictureURL?) -> Unit, failCb: (String) -> Unit, photoId : String){

        val getPictureURLRequest = JsonObjectRequest(Request.Method.GET, url_getSizes.plus(photoId), null,
                Response.Listener { response ->

                    val jsonobj : JSONObject = response.getJSONObject("sizes")
                    val size : JSONArray = jsonobj.getJSONArray("size")
                    var urlSquare = ""
                    var urlThumbnail = ""

                    for (i in 0 until size.length()){
                        val loc = size.get(i) as JSONObject
                        val label = loc.getString("label")

                        if (label.equals("Medium 640"))
                            urlSquare = loc.getString("source")
                        else if (label.equals("Thumbnail"))
                            urlThumbnail = loc.getString("source")
                    }

                    successCb(PictureURL(urlSquare,urlThumbnail))

                },
                Response.ErrorListener { error ->
                    failCb(error.toString())
                }
        )

        app.queue.add(getPictureURLRequest)

    }

}