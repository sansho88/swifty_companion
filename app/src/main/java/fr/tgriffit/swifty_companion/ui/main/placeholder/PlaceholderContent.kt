package fr.tgriffit.swifty_companion.ui.main.placeholder

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    val PROJECTS: MutableList<ProjectItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, ProjectItem> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 'A' downTo COUNT.toChar()) {
            addItem(createPlaceholderItem(i.toString()))
        }
    }

    private fun addItem(item: ProjectItem) {
        PROJECTS.add(item)
        ITEM_MAP[item.name] = item
    }

    private fun createPlaceholderItem(projectName: String): ProjectItem {
        return ProjectItem(projectName, "(42/42/2042)", makeScore())
    }

    private fun makeScore(): Int {
        return  (Math.random() * 100).toInt()
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class ProjectItem(val name: String, val validatedTime: String, val score: Int) {
        override fun toString(): String = "$name | $validatedTime | $score"
    }
}