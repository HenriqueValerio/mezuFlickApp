package recruitment.mezu.mezuflickrapp.repositories

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import recruitment.mezu.mezuflickrapp.*
import recruitment.mezu.mezuflickrapp.model.User


class UserRepository(private val app : MezuExerciseApp){

    val ENDPOINT_URL = "https://www.flickr.com/services/rest/?method="
    val url_getUserId = ENDPOINT_URL.plus("flickr.people.findByUsername&format=json&api_key=76479578b1c3e371d8c0f649acd42647&username=eyetwist&nojsoncallback=1")

    fun getUser(successCb: (User?) -> Unit, failCb: (String) -> Unit){

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url_getUserId, null,
                Response.Listener { response ->
                    val jsonobj : JSONObject = response.getJSONObject("user")
                    val userId : String = jsonobj.getString("id")
                    successCb(User(userId))
                },
                Response.ErrorListener { error ->
                    failCb(error.toString())
                }
        )

        app.queue.add(jsonObjectRequest)
    }


}