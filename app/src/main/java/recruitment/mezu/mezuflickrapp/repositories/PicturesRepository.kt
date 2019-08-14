package recruitment.mezu.mezuflickrapp.repositories

import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import recruitment.mezu.mezuflickrapp.MezuExerciseApp
import recruitment.mezu.mezuflickrapp.model.Picture
import recruitment.mezu.mezuflickrapp.model.PictureInfo
import recruitment.mezu.mezuflickrapp.model.PictureURL


class PicturesRepository(private val app : MezuExerciseApp){

    val ENDPOINT_URL = "https://www.flickr.com/services/rest/?method="
    val picturesPerPage : Int = 25
    val url_getPublicPhotos = ENDPOINT_URL.plus("flickr.people.getPublicPhotos&api_key=76479578b1c3e371d8c0f649acd42647&per_page=".plus(picturesPerPage).plus("&format=json&nojsoncallback=1&user_id="))
    val url_getInfo = ENDPOINT_URL.plus("flickr.photos.getInfo&api_key=76479578b1c3e371d8c0f649acd42647&format=json&nojsoncallback=1&photo_id=")
    val url_getSizes = ENDPOINT_URL.plus("flickr.photos.getSizes&api_key=76479578b1c3e371d8c0f649acd42647&format=json&nojsoncallback=1&photo_id=")
    var pictures : ArrayList<Picture> = ArrayList()

    fun getPictures(successCb: (List<Picture>?) -> Unit, failCb: (String) -> Unit, userId : String) {
        //getPublicPhotos(user_id) -> id, title
        //getInto(photo_id) -> taken, location, url
        //getSizes(photo_id) -> url_thumbnail

        getPublicPhotos(successCb,failCb, userId)
    }


    private fun getPublicPhotos(successCb: (List<Picture>?) -> Unit, failCb: (String) -> Unit,userId: String){

        val getPublicPhotosRequest = JsonObjectRequest(Request.Method.GET, url_getPublicPhotos.plus(userId), null,
                Response.Listener { response ->
                    val jsonobj : JSONObject = response.getJSONObject("photos")
                    val photos : JSONArray = jsonobj.getJSONArray("photo")
                    var compleated : Int = 0

                    for (i in 0 until photos.length()){
                        val loc = photos.get(i) as JSONObject
                        var operationsCompleated : Int = 0

                        val id : String = loc.getString("id")
                        val title : String = loc.getString("title")
                        var taken : String? = ""
                        var location : String? = ""
                        var url : String? = ""
                        var url_thumbnail : String? = ""

                        getInfo({
                            taken = it?.taken
                            location = it?.location
                            operationsCompleated +=1

                            if (operationsCompleated.equals(2)){
                                pictures!!.add(Picture(id,title, taken, location, url, url_thumbnail))
                                compleated+=1
                                if (compleated.equals(picturesPerPage))
                                    successCb(pictures)
                            }

                        },{
                            Log.v(app.TAG,"Error")
                        }, id)

                        getURL({
                            url = it?.url
                            url_thumbnail = it?.url_thumbnail
                            operationsCompleated+=1

                            if (operationsCompleated.equals(2)){
                                pictures?.add(Picture(id,title, taken, location, url, url_thumbnail))
                                compleated+=1
                                if (compleated.equals(picturesPerPage))
                                    successCb(pictures)
                            }

                        },{
                            Log.v(app.TAG,"Error")
                        }, id)


                    }

                },
                Response.ErrorListener { error ->
                    failCb(error.toString())
                }
        )

        app.queue.add(getPublicPhotosRequest)
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

                        locationValue = location_country.plus(",").plus(location_region).plus(",").plus(location_locality)
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
                    var urlSquare : String = ""
                    var urlThumbnail : String = ""

                    for (i in 0 until size.length()){
                        val loc = size.get(i) as JSONObject
                        val label = loc.getString("label")

                        if (label.equals("Square"))
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