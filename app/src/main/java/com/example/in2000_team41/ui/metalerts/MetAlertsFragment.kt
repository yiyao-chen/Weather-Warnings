package com.example.in2000_team41.ui.metalerts

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.in2000_team41.R
import com.example.in2000_team41.api.metalerts.AlertModel
import com.example.in2000_team41.ui.dialogs.LoadingDialog
import com.example.in2000_team41.ui.dialogs.LoadingErrorDialog
import com.example.in2000_team41.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_metalerts.*
import kotlinx.android.synthetic.main.fragment_metalerts.card_map


class MetAlertsFragment : Fragment(R.layout.fragment_metalerts), MetAlertsAdapter.OnItemClickListener {
    private lateinit var viewModel: MetAlertsViewModel
    private val viewModelHome: HomeViewModel by activityViewModels()
    private var mMetAlertsAdapter: MetAlertsAdapter? = null
    private lateinit var appBarMenu: Menu
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var loadingErrorDialog: LoadingErrorDialog
    private lateinit var spinner: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(this.requireActivity())

        viewModel = ViewModelProvider(requireActivity()).get(MetAlertsViewModel::class.java)

        setHasOptionsMenu(true)

        help_button.setOnClickListener {
            // Hindrer appen i å krasje dersom brukeren trykker to ganger raskt
            if (!viewModel.infoClicked) {
                findNavController().navigate(R.id.action_MetAlertsFragment_to_BottomSheetMetAlertsInfo)
                viewModel.infoClicked = true
            }
        }
        card_map.setOnClickListener {
            findNavController().navigate(R.id.navigation_map)
        }

        spinner = metalerts_spinner
        //setter tittel
        requireActivity().title = resources.getString(R.string.farevarsler_oversikt)
        setMetAlertsAdapter()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if(pos != 0) {
                    viewModel.sortMetAlerts(pos)
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
                if (input != null) {
                    if(input.isNotEmpty()) {
                        if (recyclerview_metalerts.adapter is MetAlertsAdapter) {
                            mMetAlertsAdapter?.filter?.filter(input)
                        }
                    }
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



    // onClick() for hver MetAlert
    override fun onItemClickMet(alertClicked: AlertModel) {
        viewModel.setAlertData(alertClicked)
        findNavController().navigate(R.id.alertDetailFragment)
    }


    // setter adapteren til mMetAlertsAdapter og observerer data fra MetAlerts-API
    private fun setMetAlertsAdapter(){
        if (mMetAlertsAdapter == null) mMetAlertsAdapter = MetAlertsAdapter(this, viewModelHome.userAdress)
        recyclerview_metalerts.adapter = mMetAlertsAdapter
        recyclerview_metalerts.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerview_metalerts.setHasFixedSize(true)

        if (!viewModel.dataCached) {
            loadingDialog.startLoading()
            viewModel.setMetAlertsApiData()
        }
        // Om det tar med en 10 sekunder å laste inn -> feil (mest sannsynlig nettverksfeil)
        Handler().postDelayed({
            if (!viewModel.dataCached){
                loadingDialog.dismiss()
                loadingErrorDialog = LoadingErrorDialog(this.requireActivity())
                loadingErrorDialog.startLoading()
            }
        }, 10000)

        viewModel.metAlertsLiveData()!!.observe(viewLifecycleOwner, {dataList  ->
            //Hente detaljert info om MetAlerts
            viewModel.setFullAlertsApiData(dataList, viewModelHome.coordinates)
            viewModel.fullAlertsLiveData()!!.observe(viewLifecycleOwner, { alerts ->
                mMetAlertsAdapter?.updateAdapter(alerts)
                recyclerview_metalerts.adapter?.notifyDataSetChanged()
                if (!viewModel.dataCached) {
                    Handler().postDelayed({ loadingDialog.dismiss() }, 0)
                    viewModel.dataCached = true
                }
            })
        })
    }






}