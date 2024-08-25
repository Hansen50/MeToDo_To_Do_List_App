package com.example.tasktodoapplication.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tasktodoapplication.activity.MainActivity
import com.example.tasktodoapplication.fragments.DoneTaskFragment
import com.example.tasktodoapplication.fragments.TaskFragment

class ViewPagerAdapter(fragment: MainActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TaskFragment()
            1 -> DoneTaskFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}