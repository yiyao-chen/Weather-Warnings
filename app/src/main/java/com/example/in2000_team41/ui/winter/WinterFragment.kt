package com.example.in2000_team41.ui.winter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.in2000_team41.R
import com.example.in2000_team41.api.avalanche.AvalancheModel
import com.example.in2000_team41.ui.dialogs.LoadingDialog
import com.example.in2000_team41.ui.dialogs.LoadingErrorDialog
import kotlinx.android.synthetic.main.fragment_winter.*

class WinterFragment : Fragment(R.layout.fragment_winter), AvalancheAdapter.OnItemClickListener {

    private val viewModel: WinterViewModel by activityViewModels()
    private var mAvalancheAdapter: AvalancheAdapter? = null
    private lateinit var appBarMenu: Menu
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var loadingErrorDialog: LoadingErrorDialog
    private lateinit var spinner: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())

        setHasOptionsMenu(true)
        spinner = avalanche_spinner

        requireActivity().title = resources.getString(R.string.snoskredvarsler)

        card_my_position.setOnClickListener {
            // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
            if (!viewModel.elementOnClick) {
                viewModel.fromMyPosition = true
                findNavController().navigate(R.id.action_WinterFragment_to_BottomSheetAvalanchesearch)
                viewModel.elementOnClick = true
            }
        }

        helpbutton_avalanche.setOnClickListener {
            // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
            if (!viewModel.infoClicked) {
                findNavController().navigate(R.id.action_WinterFragment_to_BottomSheetAvalancheinfo)
                viewModel.infoClicked = true
            }
        }

        setAvalancheAdapter()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if(pos != 0) {
                    viewModel.sortAvalanche(pos)
                }
            }
        }
    }


    //merge app bar's menu into custom menu and add listener to search-icon
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        appBarMenu = menu
        inflater.inflate(R.menu.winter_search_menu, menu)
        searchListener(menu)
    }

    private fun searchListener(menu: Menu) {
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        searchItem.setOnMenuItemClickListener {
            findNavController().navigate(R.id.action_WinterFragment_to_BottomSheetAvalanchesearch)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    private fun setAvalancheAdapter(){
        mAvalancheAdapter = AvalancheAdapter(this)
        recyclerview_winter.adapter = mAvalancheAdapter
        recyclerview_winter.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerview_winter.setHasFixedSize(true)

        if (!viewModel.dataCached){
            loadingDialog.startLoading()
            viewModel.setAvalancheRegionSummary()
        }
        // Om det tar med en 10 sekunder å laste inn -> feil (mest sannsynlig nettverksfeil)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!viewModel.dataCached){
                loadingDialog.dismiss()
                loadingErrorDialog = LoadingErrorDialog(this.requireActivity())
                loadingErrorDialog.startLoading()
            }
        }, 10000)

        viewModel.avalancheRegionSummaryLiveData()!!.observe(viewLifecycleOwner, {dataList  ->
            mAvalancheAdapter?.updateAdapter(dataList)
            recyclerview_winter.adapter?.notifyDataSetChanged()

            if (!viewModel.dataCached) {
                Handler(Looper.getMainLooper()).postDelayed({ loadingDialog.dismiss() }, 0)
                viewModel.dataCached = true
            }
        })
    }

    override fun onItemClickAvalancheSummary(current: AvalancheModel) {
        viewModel.currentData = current
        viewModel.fromOnClick = true
        if (!viewModel.elementOnClick) {
            findNavController().navigate(R.id.action_WinterFragment_to_BottomSheetAvalanchesearch)
            viewModel.elementOnClick = true
        }
    }


}