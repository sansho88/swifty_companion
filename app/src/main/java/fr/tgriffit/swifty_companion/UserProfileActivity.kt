package fr.tgriffit.swifty_companion

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.Request
import fr.tgriffit.swifty_companion.data.auth.Token
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val TAG = "UserProfileActivity"


class UserProfileActivity : AppCompatActivity() {

    lateinit var apiService: ApiService
    lateinit var token: Token
    lateinit var user: User
    private val gson = Gson()

    lateinit var userLogin: TextView
    lateinit var userName: TextView
    lateinit var userLevel: TextView
    lateinit var userGrade: TextView
    lateinit var userEvalPoints: TextView
    lateinit var userPosition: TextView
    lateinit var searchBar: SearchView
    lateinit var userAvatar: ShapeableImageView
    lateinit var userExpBar: ProgressBar

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)


        token = intent.getParcelableExtra<Token?>("token", Token::class.java)!!
        apiService = ApiService(token)

        initUserProfileUIElements()


        try {
            user = gson.fromJson(apiService.getAbout("me"), User::class.java)

            //user = gson.fromJson(apiService.getAbout(Request().userByLogin("tgriffit"/*user.getLogin()*/)), User::class.java) //fixme: classe User differente
            /* var poubelle = apiService.getAbout("me")
             Log.d(TAG, "onCreate: poubelle : $poubelle")*/
            Log.d(TAG, "onCreate: user : $user")
            updateUserData(user)


        } catch (exception: Exception) {
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }
    }

    override fun onResume() {
        super.onResume()
        val executor = Executors.newSingleThreadExecutor()

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(login: String?): Boolean {
                if (!login.isNullOrEmpty() && login.isNotBlank()) {
                    executor.execute {
                        val newUser = apiService.getAbout(Request().userByLogin(login))
                        Log.d(TAG, "onSubmit: [$login] =>\n ${newUser}")
                        val users = gson.fromJson(newUser, Array<User>::class.java)
                        val userSearched = apiService.getAbout(Request().userById(users[0].id))
                        user = gson.fromJson(userSearched, User::class.java)
                       Log.d(TAG, "onSubmit: user : $user")


                    }
                    if (executor.awaitTermination(1, TimeUnit.SECONDS))
                        executor.shutdown()
                    updateUserData(user) //fixme: n'update pas vraiment l'UI pour l'instant
                    return false
                }
                return false
            }
        })

    }

    private fun updateUserData(updatedUser: User = user) {
        val cursus = updatedUser.cursus_users
        val level = if (cursus.isNullOrEmpty()) 0.0 else cursus[0].level
        userLogin.text = updatedUser.getLogin().uppercase()
        userName.text = updatedUser.getFullName()
        userLevel.text = String.format(Locale.US, "Lvl: %,.2f %%", level)
        userGrade.text = updatedUser.getKind()
        userEvalPoints.text =
            String.format(Locale.US, "%d points", updatedUser.getCorrectionPoint())
        userPosition.text = updatedUser.location

        Glide.with(this)
            .load(updatedUser.image.link)
            .into(userAvatar)
    }

    private fun initUserProfileUIElements() {
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

