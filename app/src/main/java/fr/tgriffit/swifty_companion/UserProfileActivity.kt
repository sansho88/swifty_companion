package fr.tgriffit.swifty_companion

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.Token

private const val TAG = "UserProfileActivity"


class UserProfileActivity : AppCompatActivity() {

    lateinit var apiService: ApiService
    lateinit var token : Token
    lateinit var user : User
    private val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val myBuilder = CronetEngine.Builder(this.baseContext)
        setContentView(R.layout.user_profile)
       /* val cronetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()*/
        token = intent.getParcelableExtra<Token?>("token", Token::class.java)!!

        apiService = ApiService()
        apiService.setToken(token)

        try {
            user = gson.fromJson(apiService.getMe(), User::class.java)
            Log.d(TAG, "onCreate: user : $user")
        }catch (exception : Exception){
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }
    }
}

