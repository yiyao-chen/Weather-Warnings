package com.example.in2000_team41.ui.forestfire

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.in2000_team41.R
import com.example.in2000_team41.api.forestfire.ForestfireHolder
import com.example.in2000_team41.api.forestfire.ForestfireModel
import com.example.in2000_team41.ui.dialogs.LoadingDialog
import com.example.in2000_team41.ui.dialogs.LoadingErrorDialog
import kotlinx.android.synthetic.main.fragment_forestfire.*


class ForestfireFragment : Fragment(R.layout.fragment_forestfire) {

    // ForestfireViewModel blir opprettet i HomeFragment
    // og delt med ForestfireFragment her
    private val viewModel: ForestfireViewModel by activityViewModels()
    private var mForefireAdapter: ForestfireAdapter? = null
    private lateinit var appBarMenu: Menu
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var loadingErrorDialog: LoadingErrorDialog
    private lateinit var spinner: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())

        setHasOptionsMenu(true)

        spinner = forestfire_spinner
        requireActivity().title = resources.getString(R.string.skogbrannvarsler)
        setForestfireAdapter()

        help_button_Forestfire.setOnClickListener {
            // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
            if (!viewModel.infoClicked) {
                findNavController().navigate(R.id.action_ForestfireFragment_to_BottomSheetForestfireInfo)
                viewModel.infoClicked = true
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if(pos != 0) {
                    viewModel.sortForestfire(pos)
                }
            }
        }
    }

    //merge app bar's menu into custom menu and add listener to search-icon
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        appBarMenu = menu
        inflater.inflate(R.menu.appbar_menu, menu)
        searchListener(menu)
    }

    private fun searchListener(menu: Menu) {
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(input: String?): Boolean {
                if (recyclerview_forestfire != null) {
                    mForefireAdapter?.filter?.filter(input)
                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }






    private fun setForestfireAdapter(){
        if (mForefireAdapter == null) mForefireAdapter = ForestfireAdapter()
        recyclerview_forestfire.adapter = mForefireAdapter
        recyclerview_forestfire.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerview_forestfire.setHasFixedSize(true)

        // Sjekker om dataen allerede er hentet (trenger ikke LoadingDialog om dataen er hentet fra før av
        if (!viewModel.dataCached){
            loadingDialog.startLoading()
            viewModel.setForestfireApiData()
        }
        // Om det tar med en 10 sekunder å laste inn -> feil (mest sannsynlig nettverksfeil)
        Handler().postDelayed({
            if (!viewModel.dataCached){
                loadingDialog.dismiss()
                loadingErrorDialog = LoadingErrorDialog(this.requireActivity())
                loadingErrorDialog.startLoading()
            }
          }, 10000)

        viewModel.forestfireLiveData()!!.observe(viewLifecycleOwner, { dataList ->
            // Setter data fra api response
            var list = dataList
            if (viewModel.firstForestfireRecyclerview) {
                list = viewModel.sortFirstInput(dataList as MutableList<ForestfireModel>)
                viewModel.firstForestfireRecyclerview = false
            }
            mForefireAdapter?.updateAdapter(list)
            recyclerview_forestfire.adapter?.notifyDataSetChanged()
            help_button_Forestfire.visibility = View.VISIBLE

            if (!viewModel.dataCached) {
                Handler().postDelayed({ loadingDialog.dismiss() }, 0)
                viewModel.dataCached = true
            }
        })
    }



}