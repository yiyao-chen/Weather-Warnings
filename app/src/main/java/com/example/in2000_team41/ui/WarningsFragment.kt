package com.example.in2000_team41.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.in2000_team41.R
import kotlinx.android.synthetic.main.fragment_warnings.*

class WarningsFragment : Fragment(R.layout.fragment_warnings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = resources.getString(R.string.varsler_tilleggsmeny) + " ➡️"

        setListeners()
    }

    private fun setListeners() {
        card_weather.setOnClickListener {
            findNavController().navigate(R.id.navigation_weather)
        }
        card_weatherAlerts.setOnClickListener {
            findNavController().navigate(R.id.navigation_weatherAlerts)
        }
        card_forestfire.setOnClickListener {
            findNavController().navigate(R.id.navigation_forestfire)
        }
        card_winter.setOnClickListener {
            findNavController().navigate(R.id.navigation_winter)
        }
        card_ocean.setOnClickListener {
            findNavController().navigate(R.id.navigation_ocean)
        }
        card_map.setOnClickListener {
            findNavController().navigate(R.id.navigation_map)
        }
    }
}