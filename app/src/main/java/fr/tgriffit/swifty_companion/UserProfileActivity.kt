package fr.tgriffit.swifty_companion

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.Request
import fr.tgriffit.swifty_companion.data.auth.Token
import java.util.Locale

private const val TAG = "UserProfileActivity"


class UserProfileActivity : AppCompatActivity() {

    lateinit var apiService: ApiService
    lateinit var token : Token
    lateinit var user : User
    private val gson = Gson()

    lateinit var userLogin: TextView 
    lateinit var userName: TextView 
    lateinit var userLevel : TextView 
    lateinit var userGrade : TextView 
    lateinit var userEvalPoints : TextView
    lateinit var userPosition : TextView 
    lateinit var searchBar : SearchView 
    lateinit var userAvatar : ShapeableImageView
    lateinit var userExpBar : ProgressBar

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val myBuilder = CronetEngine.Builder(this.baseContext)
        setContentView(R.layout.user_profile)
       /* val cronetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()*/
        token = intent.getParcelableExtra<Token?>("token", Token::class.java)!!
        apiService = ApiService(token)

        userLogin = findViewById(R.id.user_login_text)
        userName = findViewById(R.id.user_fullName_text)
        userLevel = findViewById(R.id.user_level_text)
        userGrade = findViewById(R.id.user_grade_text)
        userEvalPoints = findViewById(R.id.user_evalPoints_text)
        userPosition = findViewById(R.id.user_placeConnected_text)
        searchBar = findViewById(R.id.search_user_searchView)
        userAvatar = findViewById(R.id.user_avatar)
        userExpBar = findViewById(R.id.exp_progressBar)


        try {
            user = gson.fromJson(apiService.getAbout("me"), User::class.java)
            //user = gson.fromJson(apiService.getAbout(Request().userByLogin("tgriffit"/*user.getLogin()*/)), User::class.java) //fixme: classe User differente

            Log.d(TAG, "onCreate: user : $user")
            userLogin.text = user.getLogin().uppercase()
            userName.text = user.getFullName()
            userLevel.text = String.format(Locale.US,"Lvl: %,.2f %%", user.getCursusUsers()[0].level)
            userGrade.text = user.getKind()
            userEvalPoints.text = String.format(Locale.US, "%d points", user.getCorrectionPoint())
            userPosition.text = user.location

            Glide.with(this)
                .load(user.image.link)
                .into(userAvatar)


        }catch (exception : Exception){
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }
    }
}

