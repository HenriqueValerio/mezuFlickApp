package recruitment.mezu.mezuflickrapp

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.json.JSONObject
//import recruitment.mezu.mezuflickrapp.model.dataparse.Parse


fun <T> getRequest(url: String, type: Class<T>,cbSuccess : ( (T)-> Unit ), cbError : ( (String)-> Unit ), app: MezuExerciseApp ){

    val req = GetRequest(url, type, Response.Listener{
        cbSuccess(it)},
            Response.ErrorListener { cbError(it.toString())}
            )
    app.queue.add(req)
}


class GetRequest<T>(url: String, val type: Class<T>, success: Response.Listener<T>, error: Response.ErrorListener)
    : JsonRequest<T>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        Log.v("ddaattaa",String(response.data) )

        //val jsonData = JSONObject(String(response.data))
        //val x = parser.parseToModel(jsonData)

        val dto = mapper.readValue(String(response.data), type)

        return Response.success(dto, null)
    }
}