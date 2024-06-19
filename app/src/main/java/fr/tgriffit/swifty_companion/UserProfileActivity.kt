package fr.tgriffit.swifty_companion

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.Request
import fr.tgriffit.swifty_companion.data.auth.Token
import fr.tgriffit.swifty_companion.data.model.UserData
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val TAG = "UserProfileActivity"


class UserProfileActivity : AppCompatActivity() {

    lateinit var apiService: ApiService
    var token: Token? = null
    var user: User? = null
    var currentCursus: UserData.CursusUser? = null
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
    lateinit var cursusSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)


        if (token == null)
            token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)!!

        apiService = ApiService(token)

        initUserProfileUIElements()
        cursusSpinner.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        //cursusSpinner.findViewById<TextView>(android.R.id.text1).setTextColor(Color.WHITE)

        try {
            user = gson.fromJson(apiService.getAbout("me"), User::class.java)
            updateUserData(user!!)
        } catch (exception: Exception) {
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }

        searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            .setTextColor(Color.WHITE)
        val searchBarEditText =
            searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchBarEditText.filters = arrayOf(InputFilter.LengthFilter(MAX_LOGIN_LEN))


        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var lastSearched: String = ""
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length == MAX_LOGIN_LEN)
                    Toast.makeText(
                        this@UserProfileActivity,
                        "A login can't be bigger",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }

            override fun onQueryTextSubmit(login: String?): Boolean {
                if (login.isNullOrEmpty())
                    return false
                if (user != null && user!!.getLogin().equals(login, ignoreCase = true))
                    return false
                if (lastSearched.equals(login, ignoreCase = true))
                    return false
                lastSearched = login
                val executor = Executors.newSingleThreadExecutor()
                if (login.isNotEmpty() && login.isNotBlank()) {
                    executor.execute {
                        val newUser =
                            apiService.getAbout(Request().userByLogin(login.trim())) //needed for request on ID and get ALL infos

                        if (newUser.isNullOrEmpty()) {
                            Log.e(TAG, "onQueryTextSubmit: user $login doesn't exist")
                            user = null
                            return@execute
                        }
                        val users = gson.fromJson(newUser, Array<User>::class.java)
                        val userSearched =
                            apiService.getAbout(Request().userById(users[0].id)) //get ALL infos

                        user = gson.fromJson(userSearched, User::class.java)
                        Log.d(TAG, "onSubmit: user : $user")

                    }
                    Toast.makeText(this@UserProfileActivity, "Fetching data...", Toast.LENGTH_SHORT)
                        .show()
                    if (executor.awaitTermination(
                            3,
                            TimeUnit.SECONDS
                        )
                    ) //3secs are necessary...or there's a decalage between searches
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

    private fun updateUserLevel(level: Double) {
        userLevel.text = String.format(Locale.US, "Lvl: %,.2f %%", level)
        userExpBar.progress = ((level - level.toInt() )* 100).toInt()
    }

    private fun updateUserCursus(cursusUserList: List<UserData.CursusUser>) {
        val cursus = cursusUserList
        val adapter = ArrayAdapter(this, R.layout.cursus_spinner_item, cursus.map { it.cursus.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cursusSpinner.adapter = adapter
        val level = currentCursus?.level ?: 0.0
        updateUserLevel(level)

        cursusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentCursus = cursusUserList[position]
                updateUserLevel(currentCursus?.level ?: 0.0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }

    @NonNull
    private fun updateUserData(updatedUser: User = user!!) {
        userLogin.text = updatedUser.getLogin().uppercase()
        userName.text = updatedUser.getFullName()
        userGrade.text = updatedUser.getKind()
        userEvalPoints.text =
            String.format(
                Locale.US,
                "%d point%s",
                updatedUser.getCorrectionPoint(),
                if (updatedUser.getCorrectionPoint() <= 1 && updatedUser.getCorrectionPoint() >= -1) "" else "s"
            )
        userPosition.text = updatedUser.location

        Glide.with(this)
            .load(updatedUser.image.link)
            .into(userAvatar)

        updateUserCursus(updatedUser.cursus_users)
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
        cursusSpinner = findViewById(R.id.spinner)
    }

}

