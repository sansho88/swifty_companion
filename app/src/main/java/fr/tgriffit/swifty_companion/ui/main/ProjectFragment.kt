package fr.tgriffit.swifty_companion.ui.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import fr.tgriffit.swifty_companion.R
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.data.model.UserData
import fr.tgriffit.swifty_companion.databinding.FragmentProjectListBinding
import fr.tgriffit.swifty_companion.ui.main.UserProfileFragment.Companion.ARG_SECTION_NUMBER
import fr.tgriffit.swifty_companion.ui.main.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class ProjectFragment : Fragment() {

    private var columnCount = 1
    private lateinit var cursusSpinner: Spinner
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentProjectListBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel.apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 2)
        }
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_project_list, container, false)
        val root = binding.root

        cursusSpinner = binding.spinner //todo: show cursus in spinner

        cursusSpinner.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager =  LinearLayoutManager(context)
                adapter = MyProjectRecyclerViewAdapter(PlaceholderContent.ITEMS)
            }
        }
        return view
    }

    private fun changeProjectsList(cursusUserList: List<UserData.CursusUser>) {
        val cursus = cursusUserList
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.cursus_spinner_item,
            cursus.map { it.cursus.name })
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}