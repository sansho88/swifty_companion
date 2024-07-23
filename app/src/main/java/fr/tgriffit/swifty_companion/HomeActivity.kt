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
import fr.tgriffit.swifty_companion.ui.main.UserProfileFragment


class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    val MAX_LOGIN_LEN = 8

    private lateinit var binding: ActivityHomeBinding
    lateinit var apiService: ApiService
    var token: Token? = null
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
     //TODO: tuto sur les fragments https://developer.android.com/guide/fragments/create?hl=fr#kts

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.addFragment(UserProfileFragment())
        //TODO
       /* sectionsPagerAdapter.addFragment(ProjectsFragment(), "Projects")
        sectionsPagerAdapter.addFragment(SkillsFragment(), "Skills")*/

       val viewPager: ViewPager = binding.viewPager
        //fixme: Show the fragment not initialized
        viewPager.adapter = sectionsPagerAdapter
         val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        if (token == null)
            token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)!!

        apiService = ApiService(token)
        sharedViewModel.setApiService(apiService)

        val searchView = binding.searchUserSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    sharedViewModel.setSearchQuery(query).performSearch()
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