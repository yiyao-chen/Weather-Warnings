package com.example.in2000_team41.ui.guides

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.in2000_team41.MainActivity
import com.example.in2000_team41.R
import com.example.in2000_team41.ui.dialogs.HomeLoadingDialog
import kotlinx.android.synthetic.main.fragment_guide1.*


class Guide1Fragment : Fragment(R.layout.fragment_guide1) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            (activity as MainActivity?)?.hideTopBar()
            (activity as MainActivity?)?.hideBottomNav()
        }, 1)


        skipGuide_tv.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
        nextGuide_image.setOnClickListener {
            findNavController().navigate(R.id.navigation_guide2)
        }


    }
}