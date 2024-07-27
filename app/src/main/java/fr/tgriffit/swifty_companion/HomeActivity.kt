package fr.tgriffit.swifty_companion

import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.IntentCompat
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.Token
import fr.tgriffit.swifty_companion.ui.main.SectionsPagerAdapter
import fr.tgriffit.swifty_companion.databinding.ActivityHomeBinding
import androidx.activity.viewModels
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.ui.main.ProjectFragment
import fr.tgriffit.swifty_companion.ui.main.UserProfileFragment
import java.util.concurrent.Executors


class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    val MAX_LOGIN_LEN = 8
    private val gson = Gson()

    private lateinit var binding: ActivityHomeBinding
    lateinit var apiService: ApiService
    var token: Token? = null
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.addFragment(UserProfileFragment())
        //TODO
         sectionsPagerAdapter.addFragment(ProjectFragment())
        /* sectionsPagerAdapter.addFragment(SkillsFragment(), "Skills")*/

        token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)
        apiService = ApiService(token)

        sharedViewModel.setApiService(apiService)
        sharedViewModel.apiService.observe(this) {
            try {
                sharedViewModel.setResult(sharedViewModel.apiService.value?.getAbout("me"))
                val tmpUser = sharedViewModel.getUserFromResult()
                if (tmpUser != null) {
                    Log.d("HomeActivity", "onCreate: tmpUser : $tmpUser")
                    sharedViewModel.setUser(tmpUser)
                } else
                    Log.e("HomeActivity", "onCreate: tmpUser is null")
                sharedViewModel.performSearch()
            } catch (e: Exception) {
                Log.e(TAG, "onCreate: ApiService().getMe: ", e)
            }
        }


        val viewPager: ViewPager = binding.viewPager

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        if (token == null)
            token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)!!


        val searchView = binding.searchUserSearchView
        var lastSearched: String = ""
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // val executor = Executors.newSingleThreadExecutor()
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty() && lastSearched != query) {
                    Log.d("HomeActivity", "onQueryTextSubmit: $query")
                    lastSearched = query
                    val user = sharedViewModel.searchUser(query)
                    if (user == null){
                        Toast.makeText(
                            this@HomeActivity,
                            "$query doesn't exist",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("HomeActivity", "onQueryTextSubmit: result is null")
                        return false
                    }
                    Log.d("HomeActivity", "result variable= ${sharedViewModel.result.value}")
                    sharedViewModel.setUser(user)


                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length == MAX_LOGIN_LEN)
                    Toast.makeText(
                        this@HomeActivity,
                        "A login can't be bigger",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
        })

    }
}