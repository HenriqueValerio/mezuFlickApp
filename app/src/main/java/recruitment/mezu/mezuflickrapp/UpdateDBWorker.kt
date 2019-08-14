package recruitment.mezu.mezuflickrapp

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.VolleyError
import recruitment.mezu.mezuflickrapp.repositories.UserRepository

class UpdateDBWorker(context : Context, params : WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val app = applicationContext as MezuExerciseApp
            Log.v(app.TAG, "Updating local DB with the recent pictures")

            val sharedPreferences = app.getSharedPreferences("YAMA", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            val org = sharedPreferences.getString("org", null)

            if (token != null) {
                val repo = app.repo
               // updateUser(repo.userRepository, token)
                //repo.picturesRepository.getPictures()
            }
            Result.SUCCESS
        } catch (error: VolleyError) {
            if (canRecover(error))
                Result.RETRY
            else
                Result.FAILURE
        }
    }


    private fun canRecover(error: VolleyError): Boolean {
        val statusCode =
                if (error.networkResponse != null) error.networkResponse.statusCode
                else 0
        return statusCode in 500..599
    }


    /*private fun updateUser(repo: UserRepository, token: String) {
        val users = repo.syncFetchDataFromAPI(token)
        repo.syncSaveToDB(users, token)
    }*/
}