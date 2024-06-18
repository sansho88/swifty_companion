package fr.tgriffit.swifty_companion

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.Request
import fr.tgriffit.swifty_companion.data.auth.Token
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val TAG = "UserProfileActivity"

//These 2 functions below add support for API below 33 (deprecated)
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

class UserProfileActivity : AppCompatActivity() {

    lateinit var apiService: ApiService
    var token: Token? = null
    var user: User? = null
    private val gson = Gson()
    val MAX_LOGIN_LEN = 8

    lateinit var userLogin: TextView
    lateinit var userName: TextView
    lateinit var userLevel: TextView
    lateinit var userGrade: TextView
    lateinit var userEvalPoints: TextView
    lateinit var userPosition: TextView
    lateinit var searchBar: SearchView
    lateinit var userAvatar: ShapeableImageView
    lateinit var userExpBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)


        if (token == null)
            token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)!!

        apiService = ApiService(token)

        initUserProfileUIElements()

        try {
            user = gson.fromJson(apiService.getAbout("me"), User::class.java)
            updateUserData(user!!)
        } catch (exception: Exception) {
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }

        searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text).setTextColor(Color.WHITE)
        val searchBarEditText = searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchBarEditText.filters = arrayOf(InputFilter.LengthFilter(MAX_LOGIN_LEN))


        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length == MAX_LOGIN_LEN)
                    Toast.makeText(this@UserProfileActivity, "A login can't be bigger", Toast.LENGTH_SHORT).show()

                return true
            }

            override fun onQueryTextSubmit(login: String?): Boolean {
                val executor = Executors.newSingleThreadExecutor()
                if (!login.isNullOrEmpty() && login.isNotBlank()) {
                    executor.execute {
                        val newUser = apiService.getAbout(Request().userByLogin(login)) //needed fpr request on ID and get ALL infos

                        if (newUser.isNullOrEmpty())
                        {
                            Log.e(TAG, "onQueryTextSubmit: user $login doesn't exist")
                            user = null
                            return@execute
                        }
                        val users = gson.fromJson(newUser, Array<User>::class.java)
                        val userSearched = apiService.getAbout(Request().userById(users[0].id)) //get ALL infos

                        user = gson.fromJson(userSearched, User::class.java)
                        Log.d(TAG, "onSubmit: user : $user")

                    }
                    Toast.makeText(this@UserProfileActivity, "Fetching data...", Toast.LENGTH_SHORT).show()
                    if (executor.awaitTermination(3, TimeUnit.SECONDS)) //3secs are necessary...or there's a decalage between searches
                        executor.shutdown()
                    if (user != null)
                        updateUserData(user!!)
                    else {
                        val snackbar = Snackbar.make(
                            findViewById(R.id.user_fullName_text),
                            "$login doesn't exist",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setTextColor(Color.WHITE)
                        snackbar.setBackgroundTint(
                            getResources().getColor(
                                android.R.color.holo_blue_dark,
                                Resources.getSystem().newTheme()
                            )
                        )
                        snackbar.show()
                    }
                    return false
                }
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
    }

    @NonNull
    private fun updateUserData(updatedUser: User = user!!) {
        val cursus = updatedUser.cursus_users
        val level = if (cursus.isEmpty()) 0.0 else cursus[0].level
        userLogin.text = updatedUser.getLogin().uppercase()
        userName.text = updatedUser.getFullName()
        userLevel.text = String.format(Locale.US, "Lvl: %,.2f %%", level)
        userGrade.text = updatedUser.getKind()
        userEvalPoints.text =
            String.format(Locale.US, "%d point%s", updatedUser.getCorrectionPoint(), if (updatedUser.getCorrectionPoint() <= 1) "" else "s")
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

