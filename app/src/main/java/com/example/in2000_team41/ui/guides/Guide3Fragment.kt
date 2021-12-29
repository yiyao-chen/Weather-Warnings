package com.example.in2000_team41.ui.guides

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.in2000_team41.R
import kotlinx.android.synthetic.main.fragment_guide2.skipGuide_tv
import kotlinx.android.synthetic.main.fragment_guide3.*


class Guide3Fragment : Fragment(R.layout.fragment_guide3) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipGuide_tv.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
        lastGuide_image3.setOnClickListener {
            findNavController().navigate(R.id.navigation_guide2)
        }
        nextGuide_image3.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
    }
}