package fr.tgriffit.swifty_companion.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.data.model.UserData
import fr.tgriffit.swifty_companion.databinding.FragmentSkillsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SkillsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SkillsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentSkillsBinding? = null
    private lateinit var radarChart: RadarChart

    private var skills: List<UserData.Skill>? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSkillsBinding.inflate(inflater, container, false)
        val root = binding.root

        radarChart = binding.radarChart
        skills = sharedViewModel.currentCursus.value!!.skills

        setupRadarChart()
        if (skills != null) setData(skills!!)

        sharedViewModel.currentCursus.observe(viewLifecycleOwner) {
            if (skills != null) {
                setData(it.skills)
                radarChart.notifyDataSetChanged()
            }
        }
        return root
    }

    private fun setupRadarChart() {
        radarChart.description.isEnabled = false
        radarChart.webLineWidth = 1f
        radarChart.webColor = Color.LTGRAY
        radarChart.webLineWidthInner = 1f
        radarChart.webColorInner = Color.LTGRAY
        radarChart.webAlpha = 100
        radarChart.y = 42f
    }

    private fun setData(skillsList: List<UserData.Skill>) {
        if (skillsList.isEmpty()) {
            radarChart.clear()
            return
        }
        val entries = ArrayList<RadarEntry>()
        var longuestLen = 0
        for (skill in skillsList) {
            val entry = RadarEntry(skill.level.toFloat())
            var skillName = skill.name
            longuestLen = longuestLen.coerceAtLeast(skillName.length)
            if (skillName.length > 22) {
                skillName = skillName.substring(0, 22) + ".."
                longuestLen = 22
            }
            entry.data = skillName
            entry.icon = null
            entries.add(entry)
        }
        var sizeList = entries.size
        while (sizeList < 3) {
            val entry = RadarEntry(0f)
            entry.data = ""
            entry.icon = null
            entries.add(entry)
            ++sizeList
        }

        val dataSet = RadarDataSet(entries, "Skills")
        dataSet.color = Color.rgb(103, 110, 129)
        dataSet.fillColor = Color.parseColor("#ff0099cc")
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 180
        dataSet.lineWidth = 2f
        dataSet.isDrawHighlightCircleEnabled = true
        dataSet.setDrawHighlightIndicators(false)
        dataSet.valueTextColor = Color.GREEN

        val data = RadarData(dataSet)
        data.setValueTextSize(14f)
        data.setDrawValues(true)
        data.setValueTextColor(Color.WHITE)

        radarChart.data = data
        radarChart.invalidate()

        val xAxis = radarChart.xAxis
        xAxis.textSize = 24f - (longuestLen / 2).toFloat()
        xAxis.textColor = Color.WHITE
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val activities = entries.map { it.data.toString() }
            override fun getFormattedValue(value: Float): String {
                return activities[value.toInt() % activities.size]
            }
        }

        val yAxis = radarChart.yAxis
        val highestLevel = skillsList.maxOf { it.level }
        yAxis.setLabelCount(highestLevel.toInt(), false)
        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = highestLevel.toFloat()
        yAxis.textColor = Color.WHITE
        yAxis.setDrawLabels(false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SkillsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = SkillsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }


}