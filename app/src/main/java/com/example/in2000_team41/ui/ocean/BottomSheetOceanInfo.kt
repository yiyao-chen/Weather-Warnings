package com.example.in2000_team41.ui.ocean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.in2000_team41.R
import com.example.in2000_team41.ui.forestfire.ForestfireViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetOceanInfo : BottomSheetDialogFragment() {
    private val viewModel: OceanViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // rounded corners
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_oceaninfo, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Lar infoknappen bli trykkbar igjen når BottomSheet lukkes - hindrer krasj
        viewModel.infoClicked = false
    }
}