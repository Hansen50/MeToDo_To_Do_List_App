package com.example.tasktodoapplication.activity

import com.example.tasktodoapplication.adapters.TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tasktodoapplication.R
import com.example.tasktodoapplication.adapters.ViewPagerAdapter
import com.example.tasktodoapplication.fragments.DoneTaskFragment
import com.example.tasktodoapplication.fragments.TaskFragment
import com.example.tasktodoapplication.room.Constant
import com.example.tasktodoapplication.room.Task
import com.example.tasktodoapplication.room.TaskDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var taskFragment: TaskFragment
    private lateinit var DoneTaskFragment: DoneTaskFragment
    private lateinit var Toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toolbar = findViewById(R.id.toolbar)
        Toolbar.title = "MeToDo"
        setSupportActionBar(Toolbar)
        Toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.white)) // This sets both normal and selected text colors

        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.tab_selected_indicator_color))
        tabLayout.setSelectedTabIndicatorHeight(resources.getDimensionPixelSize(R.dimen.tab_indicator_height))

        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        setupListener()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "On Progress"
                1 -> "Done"
                else -> "Unknown"
            }

        }.attach()
        setupFragments()
        setupListener()
    }

    private fun setupFragments() {
        taskFragment = supportFragmentManager.findFragmentByTag("f0") as? TaskFragment ?: TaskFragment()
        DoneTaskFragment = supportFragmentManager.findFragmentByTag("f1") as? DoneTaskFragment ?: DoneTaskFragment()

    }

    fun refreshFragments() {
        val taskFragment = supportFragmentManager.findFragmentByTag("f0") as? TaskFragment
        val doneTaskFragment = supportFragmentManager.findFragmentByTag("f1") as? DoneTaskFragment

        taskFragment?.loadData()
        doneTaskFragment?.loadData()
    }

    private fun setupListener() {
        val iconAdd = findViewById<FloatingActionButton>(R.id.icon_add)
        iconAdd.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
            // Handle floating action button click
        }
    }

    private fun intentEdit(intentType: Int, taskId: Int) {
        startActivity(
            Intent(this, TaskActivity::class.java)
                .putExtra("intent_type", intentType)
                .putExtra("intent_id", taskId)
        )
    }

}


