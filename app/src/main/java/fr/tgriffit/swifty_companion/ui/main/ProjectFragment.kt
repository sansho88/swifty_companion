package fr.tgriffit.swifty_companion.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.databinding.FragmentProjectListBinding
import fr.tgriffit.swifty_companion.ui.main.UserProfileFragment.Companion.ARG_SECTION_NUMBER

/**
 * A fragment representing a list of Items.
 */
class ProjectFragment : Fragment() {

    private var columnCount = 1
    private lateinit var adapter: MyProjectRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
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
    ): View {
        sharedViewModel.apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 2)
        }
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        val root = binding.root

        recyclerView = binding.list
        if (sharedViewModel.user.value != null && sharedViewModel.projectsList.value != null)
            sharedViewModel.setProjectsList(sharedViewModel.user.value!!.getProjectsUsers())
        adapter = MyProjectRecyclerViewAdapter(sharedViewModel.projectsList.value)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        sharedViewModel.projectsList.observe(viewLifecycleOwner, Observer {
            adapter = MyProjectRecyclerViewAdapter(sharedViewModel.projectsList.value)
            recyclerView.adapter = adapter
        })
        return root
    }


    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}