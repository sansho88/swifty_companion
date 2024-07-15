package fr.tgriffit.swifty_companion.ui.main

import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.model.UserData
import fr.tgriffit.swifty_companion.databinding.UserProfileBinding
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import androidx.lifecycle.Observer
import fr.tgriffit.swifty_companion.R


private const val TAG = "UserProfileActivity"


class UserProfileFragment : Fragment() {


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

    //lateinit var searchBar: SearchView
    lateinit var userAvatar: ShapeableImageView
    lateinit var userExpBar: ProgressBar
    lateinit var cursusSpinner: Spinner
    private var _binding: UserProfileBinding? = null

    private lateinit var pageViewModel: PageViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by lazy { ViewModelProvider(this)[SharedViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.user_profile)
        _binding = UserProfileBinding.inflate(inflater, container, false)
        val root = binding.root




        initUserProfileUIElements()
        cursusSpinner.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        //cursusSpinner.findViewById<TextView>(android.R.id.text1).setTextColor(Color.WHITE)

        try {
            user = sharedViewModel.performSearch().user.value
            updateUserData(user!!)
        } catch (exception: Exception) {
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }

        var lastSearched: String = ""
        //Detecte le changement de login dans la barre de recherche
        sharedViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
            if (isValidSearch(query, lastSearched)) {
                lastSearched = query
                val executor = Executors.newSingleThreadExecutor()
                executor.execute { searchForUser(query) }

                Toast.makeText(requireContext(), "Fetching data...", Toast.LENGTH_SHORT).show()
                //3secs are necessary...or there's a decalage between searches
                if (executor.awaitTermination(3, TimeUnit.SECONDS))
                    executor.shutdown()

                user = sharedViewModel.user.value
                if (user != null)
                    binding.apply { updateUserData(user!!) }
                else {
                    val snackbar =
                        Snackbar.make(userName, "$query doesn't exist", Snackbar.LENGTH_SHORT)
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.setBackgroundTint(
                        getResources().getColor(
                            android.R.color.holo_blue_dark,
                            Resources.getSystem().newTheme()
                        )
                    )
                    snackbar.show()
                }
            }

        })
        /* searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
             .setTextColor(Color.WHITE)
         val searchBarEditText =
             searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
         searchBarEditText.filters = arrayOf(InputFilter.LengthFilter(MAX_LOGIN_LEN))*/


        return root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun isValidSearch(login: String?, lastSearched: String): Boolean {
        if (login.isNullOrEmpty() || login.isBlank())
            return false
        if (user != null && user!!.getLogin().equals(login, ignoreCase = true))
            return false
        if (lastSearched.equals(login, ignoreCase = true))
            return false
        return true
    }

    private fun searchForUser(login: String) {
        //needed for request on ID and get ALL infos
        val newUser = sharedViewModel.performSearch().result.value

        if (newUser.isNullOrEmpty()) {
            Log.e(TAG, "onQueryTextSubmit: user $login doesn't exist")
            user = null
            return
        }
        val users = gson.fromJson(newUser, Array<User>::class.java)
        user = sharedViewModel.performSearch().getUserFromResult()

        Log.d(TAG, "onSubmit: user : $user")
    }

    private fun updateUserLevel(level: Double) {
        userLevel.text = String.format(Locale.US, "Lvl: %,.2f %%", level)
        userExpBar.progress = ((level - level.toInt()) * 100).toInt()
    }

    private fun updateUserCursus(cursusUserList: List<UserData.CursusUser>) {
        val cursus = cursusUserList
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.cursus_spinner_item,
            cursus.map { it.cursus.name })
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
        userLogin = binding.userLoginText
        userName = binding.userFullNameText
        userLevel = binding.userLevelText
        userGrade = binding.userGradeText
        userEvalPoints = binding.userEvalPointsText
        userPosition = binding.userPlaceConnectedText
        userAvatar = binding.userAvatar
        userExpBar = binding.expProgressBar
        cursusSpinner = binding.spinner
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): UserProfileFragment {
            return UserProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

