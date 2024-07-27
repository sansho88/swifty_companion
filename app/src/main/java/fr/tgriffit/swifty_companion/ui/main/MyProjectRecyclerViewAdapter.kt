package fr.tgriffit.swifty_companion.ui.main

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import fr.tgriffit.swifty_companion.R
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.data.model.UserData

import fr.tgriffit.swifty_companion.ui.main.placeholder.PlaceholderContent.ProjectItem
import fr.tgriffit.swifty_companion.databinding.FragmentProjectBinding

/**
 * [RecyclerView.Adapter] that can display a [ProjectItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyProjectRecyclerViewAdapter(
    private val projectsList: List<UserData.ProjectsUsers>?
) : RecyclerView.Adapter<MyProjectRecyclerViewAdapter.ViewHolder>() {

    private val sharedViewModel = SharedViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentProjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = projectsList!![position]
        holder.nameView.text = item.project.name
        holder.validDateView.text = if (item.status == "finished") item.marked_at else item.status
        holder.scoreView.text = if (item.marked) item.final_mark.toString() else "??"
        val scoreColor = if (!item.marked) R.color.orange
        else when {
            item.final_mark!! >= 50 -> R.color.light_green
            else -> R.color.light_red
        }
        holder.scoreView.setTextColor(holder.itemView.context.getColor(scoreColor))
    }

    //fixme: sharedViewModel.currentCursus == null
    override fun getItemCount(): Int = projectsList/*?.filter { project ->
        Log.d("MyProjectRecyclerViewAdapter", "getItemCount: ${sharedViewModel.currentCursus.value?.cursus_id}")
        (project.cursus_ids.find { id ->
            id == sharedViewModel.currentCursus.value?.cursus_id
        } ?: 0) > 0

    }*/?.size ?: 0

    inner class ViewHolder(binding: FragmentProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.projectNameTxt
        val validDateView: TextView = binding.projectValidDateTxt
        val scoreView: TextView = binding.projectScoreTxt


        override fun toString(): String {
            return super.toString() + " '" + validDateView.text + "'"
        }
    }

}