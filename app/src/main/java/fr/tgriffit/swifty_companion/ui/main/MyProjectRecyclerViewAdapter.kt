package fr.tgriffit.swifty_companion.ui.main

import android.content.res.ColorStateList
import android.text.format.DateFormat
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.alpha
import fr.tgriffit.swifty_companion.R
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.data.model.UserData

import fr.tgriffit.swifty_companion.ui.main.placeholder.PlaceholderContent.ProjectItem
import fr.tgriffit.swifty_companion.databinding.FragmentProjectBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
        var timeParsed: Long? = null
        val startTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(item.created_at)
        var finishedDate: String = ""
        val today = Date()
        val elapsedTime : Date?
        if(item.marked_at != null){
            timeParsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(item.marked_at)!!.time
            finishedDate = DateFormat.format("dd/MM/yyyy", timeParsed).toString()
            elapsedTime = Date(timeParsed - startTime!!.time)
        }else
            elapsedTime = Date(today.time - startTime!!.time)
        holder.validDateView.text = if (item.status == "finished") "$finishedDate (${TimeUnit.MILLISECONDS.toDays(elapsedTime.time)} days)" else "Since ${TimeUnit.MILLISECONDS.toDays(elapsedTime.time)} days"
        holder.scoreView.text = if (item.marked) item.final_mark.toString() else "??"
        val scoreColor = if (!item.marked) R.color.orange
        else when {
            item.final_mark!! >= 50 -> R.color.light_green
            else -> R.color.red
        }
        val backgroundColor = when(scoreColor){
            R.color.light_green -> R.color.trans_green
            R.color.red -> R.color.trans_red
            else -> R.color.trans_orange
        }
        holder.scoreView.setTextColor(holder.itemView.context.getColor(scoreColor))
        holder.itemView.context.getColor(scoreColor)
        holder.itemView.backgroundTintList = ColorStateList.valueOf(holder.itemView.context.getColor(backgroundColor))

    }

    //fixme: sharedViewModel.currentCursus == null
    override fun getItemCount(): Int = projectsList?.size ?: 0

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