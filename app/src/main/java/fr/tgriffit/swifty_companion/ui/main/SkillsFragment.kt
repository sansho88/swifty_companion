package fr.tgriffit.swifty_companion.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import fr.tgriffit.swifty_companion.data.model.SharedViewModel
import fr.tgriffit.swifty_companion.databinding.FragmentSkillsBinding
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import fr.tgriffit.swifty_companion.data.model.UserData

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*  param1 = it.getString(ARG_PARAM1)
              param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSkillsBinding.inflate(inflater, container, false)
        val root = binding.root

        radarChart = binding.radarChart
        skills = sharedViewModel.currentCursus.value!!.skills

        setupRadarChart()
        setData(skills!!)

        sharedViewModel.currentCursus.observe(viewLifecycleOwner) {
            setData(it.skills)
            radarChart.notifyDataSetChanged()
            Log.d("SkillsFragment", "skills -> ${it.skills}")
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
        val entries = ArrayList<RadarEntry>()
        for (skill in skillsList) {
            val entry = RadarEntry(skill.level.toFloat())
            entry.data = skill.name
            entry.icon = null
            entries.add(entry)
        }
        var sizeList = entries.size
        while (sizeList < 3){
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
        xAxis.textSize = 12f
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

       /* val l = radarChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.textSize = 8f*/

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
        fun newInstance(param1: String, param2: String) =
            SkillsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}