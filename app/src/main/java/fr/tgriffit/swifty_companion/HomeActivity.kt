package fr.tgriffit.swifty_companion

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageButton
import android.widget.Spinner
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
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.model.UserData
import fr.tgriffit.swifty_companion.ui.main.ProjectFragment
import fr.tgriffit.swifty_companion.ui.main.SkillsFragment
import fr.tgriffit.swifty_companion.ui.main.UserProfileFragment


class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    val MAX_LOGIN_LEN = 8
    private val gson = Gson()

    private lateinit var binding: ActivityHomeBinding
    private lateinit var searchView: SearchView
    private lateinit var cursusSpinner: Spinner
    private lateinit var meButton: ImageButton
    private lateinit var logoutButton: ImageButton
    private var user: User? = null
    lateinit var apiService: ApiService
    var token: Token? = null
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.addFragment(UserProfileFragment())

        sectionsPagerAdapter.addFragment(ProjectFragment())
        sectionsPagerAdapter.addFragment(SkillsFragment())

        cursusSpinner = binding.spinner
        cursusSpinner.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        meButton = binding.meButton
        logoutButton = binding.logoutButton


        token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)
        apiService = ApiService(token)
        sharedViewModel.setApiService(apiService)
        sharedViewModel.apiService.observe(this, apiServiceObserver())
        meButton.setOnClickListener {
            Toast.makeText(this, "Fetching data...", Toast.LENGTH_SHORT).show()
            val responseApi = sharedViewModel.apiService.value?.getAbout("me")
            if (responseApi?.success != null)
                sharedViewModel.setResult(responseApi.success!!.result)
            val me = sharedViewModel.getUserFromResult()
            searchView.setQuery(me!!.getLogin(), true)
            searchView.clearFocus()
            searchView.setQuery("", false)
        }
        logoutButton.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            apiService.logout()
            user = null
            startActivity(loginIntent)
            finish()
        }


        val viewPager: ViewPager = binding.viewPager

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#2561B4"))
        if (token == null)
            token = IntentCompat.getParcelableExtra(intent, "token", Token::class.java)!!

        searchView = binding.searchUserSearchView
        var lastSearched: String = ""
        val searchEditText = searchView.allViews.find { view -> view is EditText } as EditText
        //set maxLength of login
        searchEditText.filters = arrayOf<InputFilter>(LengthFilter(MAX_LOGIN_LEN))
        searchEditText.textSize = 22f

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty() && lastSearched != query) {
                    Toast.makeText(this@HomeActivity, "Fetching data...", Toast.LENGTH_SHORT).show()

                    lastSearched = query
                    user = sharedViewModel.searchUser(query)
                    if (user == null) {
                        Toast.makeText(
                            this@HomeActivity,
                            "$query doesn't exist",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("HomeActivity", "onQueryTextSubmit: ${sharedViewModel.apiService.value?.lastResponseApi?.failure?.message}")
                        return false
                    }
                    sharedViewModel.setUser(user!!)
                    Log.d("HomeActivity", "user variable= ${sharedViewModel.user.value}")
                    changeProjectsList(user!!.cursus_users)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) return false

                if ( newText.length > MAX_LOGIN_LEN)
                    Toast.makeText(
                        this@HomeActivity,
                        "A login can't be bigger",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
        })

    }

    private fun apiServiceObserver(): Observer<ApiService> {
        val observer = Observer<ApiService> {
            try {
                val me = sharedViewModel.apiService.value?.getAbout("me")
                if (me?.success != null)
                    sharedViewModel.setResult(me.success!!.result)
                else {
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                }
                val tmpUser = sharedViewModel.getUserFromResult()
                if (tmpUser != null) {
                    sharedViewModel.setUser(tmpUser)
                    user = tmpUser
                    changeProjectsList(sharedViewModel.user.value!!.cursus_users)
                } else
                    Log.e("HomeActivity", "onCreate: tmpUser is null")
                val searchResult = sharedViewModel.performSearch()
                /*if (searchResult?.failure != null)
                    Toast.makeText(this, searchResult.failure!!.message, Toast.LENGTH_SHORT).show()*/

            } catch (e: Exception) {
                Log.e(TAG, "onCreate: ApiService().getMe: ", e)
            }
        }
        return observer
    }

    private fun changeProjectsList(cursusUserList: List<UserData.CursusUser>) {
        val adapter = ArrayAdapter(
            this,
            R.layout.cursus_spinner_item,
            cursusUserList.map { it.cursus.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cursusSpinner.adapter = adapter


        cursusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sharedViewModel.setCurrentCursus(cursusUserList[position])
                val cursusProjects =
                    sharedViewModel.setProjectsList(
                        sharedViewModel.user.value!!.getProjectsUsers().filter { project ->
                            project.cursus_ids.find { id ->
                                id == cursusUserList[position].cursus_id
                            } != null
                        })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }
}