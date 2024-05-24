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
import fr.tgriffit.swifty_companion.data.auth.Token
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
        setContentView(R.layout.user_profile)
        val executor = Executors.newSingleThreadExecutor()
        
        token = intent.getParcelableExtra<Token?>("token", Token::class.java)!!
        apiService = ApiService(token)
        
        initUserProfileUIElements()


        try {
            user = gson.fromJson(apiService.getAbout("me"), User::class.java)
            //user = gson.fromJson(apiService.getAbout(Request().userByLogin("tgriffit"/*user.getLogin()*/)), User::class.java) //fixme: classe User differente

            Log.d(TAG, "onCreate: user : $user")
            updateUserData(user)


            searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(login: String?): Boolean { //fixme: crash when submit pressed
                    if (!login.isNullOrEmpty() && login.isNotBlank())
                    {
                       /* Log.d(TAG, "Login searched: $login")
                        executor.execute {
                            Log.d(TAG, "onSubmit: [$login] =>\n ${apiService.getAbout("me")}")
                            executor.shutdown()
                        }
                        if (executor.awaitTermination(10, TimeUnit.SECONDS))*/
                            return false
                    }
                    return false
                }
            })


        }catch (exception : Exception){
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }
    }
    
    private fun updateUserData(updatedUser : User = user){
        userLogin.text = updatedUser.getLogin().uppercase()
        userName.text = updatedUser.getFullName()
        userLevel.text = String.format(Locale.US,"Lvl: %,.2f %%", updatedUser.getCursusUsers()[0].level)
        userGrade.text = updatedUser.getKind()
        userEvalPoints.text = String.format(Locale.US, "%d points", updatedUser.getCorrectionPoint())
        userPosition.text = updatedUser.location

        Glide.with(this)
            .load(updatedUser.image.link)
            .into(userAvatar)
    }
    private fun initUserProfileUIElements(){
        userLogin = findViewById(R.id.user_login_text)
        userName = findViewById(R.id.user_fullName_text)
        userLevel = findViewById(R.id.user_level_text)
        userGrade = findViewById(R.id.user_grade_text)
        userEvalPoints = findViewById(R.id.user_evalPoints_text)
        userPosition = findViewById(R.id.user_placeConnected_text)
        searchBar = findViewById(R.id.search_user_searchView)
        userAvatar = findViewById(R.id.user_avatar)
        userExpBar = findViewById(R.id.exp_progressBar)
    }

}

