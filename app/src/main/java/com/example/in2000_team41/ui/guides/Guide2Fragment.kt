package com.example.in2000_team41.ui.guides

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.in2000_team41.R
import kotlinx.android.synthetic.main.fragment_guide2.*


class Guide2Fragment : Fragment(R.layout.fragment_guide2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipGuide_tv.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
        lastGuide_image2.setOnClickListener {
            findNavController().navigate(R.id.navigation_guide1)
        }
        nextGuide_image2.setOnClickListener {
            findNavController().navigate(R.id.navigation_guide3)
        }


    }
}