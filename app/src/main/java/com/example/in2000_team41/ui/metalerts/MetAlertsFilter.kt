package com.example.in2000_team41.ui.metalerts

import android.widget.Filter
import com.example.in2000_team41.api.metalerts.AlertModel
import java.util.*
import kotlin.collections.ArrayList

class MetAlertsFilter(listDataFull: List<AlertModel>, metAdapter: MetAlertsAdapter) : Filter() {
    var adapter = metAdapter
    var list = listDataFull

    // filter data according to the constraint and return result
    override fun performFiltering (constraint: CharSequence?): FilterResults {
        val filteredList: MutableList<AlertModel> = ArrayList()
        // if search field is empty, use the original list, else use filtered list
        if(constraint == null || constraint.isEmpty()) {
            filteredList.addAll(list)
        } else {
            val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
            for (item in list) {
                if (item.info?.area?.areaDesc!!.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                    filteredList.add(item)
                }
            }
        }
        val results = FilterResults()
        results.values = filteredList
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.listData = results?.values as List<AlertModel>
        adapter.notifyDataSetChanged()
    }

    // denne blir brukt til å teste den beskyttet metoden performFiltering
    fun performFilteringTest(constraint: CharSequence?): List<AlertModel> {
        val res = this.performFiltering(constraint)
        return res.values as List<AlertModel>
    }

}