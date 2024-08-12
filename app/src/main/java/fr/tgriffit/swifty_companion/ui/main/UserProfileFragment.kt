package fr.tgriffit.swifty_companion.ui.main

import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import fr.tgriffit.swifty_companion.R
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.databinding.UserProfileBinding
import java.util.Locale


private const val TAG = "UserProfileActivity"


class UserProfileFragment : Fragment() {


    private var user: User? = null

    private lateinit var userLogin: TextView
    private lateinit var userName: TextView
    private lateinit var userLevel: TextView
    private lateinit var userGrade: TextView
    private lateinit var userEvalPoints: TextView
    private lateinit var userPosition: TextView
    private lateinit var userAvatar: ShapeableImageView
    private lateinit var userExpBar: ProgressBar
    private lateinit var userCampus: TextView
    private var _binding: UserProfileBinding? = null

    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel.apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        _binding = UserProfileBinding.inflate(inflater, container, false)
        val root = binding.root

        sharedViewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null)
                updateUserData(it)
        })
        initUserProfileUIElements()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            user = sharedViewModel.user.value
            user?.let { updateUserData(it) }
            user?.cursus_users?.first()?.level?.let { updateUserLevel(it) }

        } catch (exception: Exception) {
            Log.e(TAG, "onCreate: ApiService().getMe: ", exception)
        }

        var lastSearched: String = ""
        //Detecte le changement de login dans la barre de recherche
        sharedViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
            if (isValidSearch(query, lastSearched)) {
                lastSearched = query
                user = sharedViewModel.user.value
                if (user != null)
                    binding.apply { updateUserData(user!!) }
                else {
                    val snackbar =
                        Snackbar.make(userName, "$query not found", Snackbar.LENGTH_SHORT)
                    snackbar.setTextColor(Color.WHITE)
                    snackbar.setBackgroundTint(
                        resources.getColor(
                            android.R.color.holo_blue_dark,
                            Resources.getSystem().newTheme()
                        )
                    )
                    snackbar.show()
                }
            }

        })

        sharedViewModel.currentCursus.observe(viewLifecycleOwner, Observer {
            updateUserLevel(it.level)
        })

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

    private fun updateUserLevel(level: Double) {
        userLevel.text = String.format(Locale.US, "Lvl: %,.2f %%", level)
        userExpBar.progress = ((level - level.toInt()) * 100).toInt()
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
        userPosition.text = if(updatedUser.location.isNullOrEmpty()) "\uD83D\uDEB7" else updatedUser.location
        val primaryCampusId = updatedUser.getCampusUsers().find { it.is_primary }?.campus_id
        userCampus.text = getString(
            R.string.campusTxt,
            updatedUser.getCampus().find { it.id == primaryCampusId }?.name ?: "No campus"
        )

        val avatarUrl = updatedUser.image.link ?: Uri.parse("android.resource://fr.tgriffit.swifty_companion/drawable/cat").toString()

        Glide.with(this)
            .load(avatarUrl)
            .into(userAvatar)

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
        userCampus = binding.userCampusText
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int, api: ApiService): UserProfileFragment {
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

