package com.example.safetyreview2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.safetyreview2.fragment.FirstFragment
import com.example.safetyreview2.fragment.SecondFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return FirstFragment()
            1 -> return SecondFragment()
            else -> return FirstFragment()
        }
    }
}