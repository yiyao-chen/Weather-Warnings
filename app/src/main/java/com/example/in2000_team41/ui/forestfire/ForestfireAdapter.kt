package com.example.in2000_team41.ui.forestfire

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team41.R
import com.example.in2000_team41.api.forestfire.ForestfireHolder
import com.example.in2000_team41.api.forestfire.ForestfireModel
import kotlinx.android.synthetic.main.element_forestfire.view.*
import java.util.*
import kotlin.collections.ArrayList

class ForestfireAdapter(
    ) : RecyclerView.Adapter<ForestfireAdapter.ViewHolder>(), Filterable {

    var listData = listOf<ForestfireHolder>()
    var listDataFull = listOf<ForestfireHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.element_forestfire,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listData[position]

        holder.nameTextView.text = currentItem.today?.name!!
        holder.nameTextView.paintFlags = holder.nameTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.countyTextView.text = currentItem.today.county
        holder.danger1.setBackgroundColor(Color.parseColor(getColor(currentItem.today.danger_index!!)))
        holder.danger1.text = simplifyDangerlevel(currentItem.today.danger_index)
        holder.danger2.setBackgroundColor(Color.parseColor(getColor(currentItem.tomorrow?.danger_index!!)))
        holder.danger2.text = simplifyDangerlevel(currentItem.tomorrow.danger_index)
        holder.danger3.setBackgroundColor(Color.parseColor(getColor(currentItem.twodays?.danger_index!!)))
        holder.danger3.text = simplifyDangerlevel(currentItem.twodays.danger_index)
    }


    override fun getItemCount() = listData.size

    fun updateAdapter(newList: List<ForestfireModel>) {
        listData = convertForestfireData(newList)
        listDataFull = convertForestfireData(newList) // copy the list to use it independently
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        // Forestfire values
        val card: CardView = itemView.forestfire_card
        val nameTextView: TextView = itemView.name_tv
        val countyTextView: TextView = itemView.county_tv
        val danger1: TextView = itemView.danger1_tv
        val danger2: TextView = itemView.danger2_tv
        val danger3: TextView = itemView.danger3_tv


    }

    interface OnItemClickListener {
        fun onItemClickForestfire(data: ForestfireHolder)
    }



    //return a Filter which constrains data with a filtering pattern
    override fun getFilter(): Filter {
        return forestFilter
    }

    private val forestFilter = object: Filter() {
        // filter data according to the constraint and return result
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<ForestfireHolder> = ArrayList()
            // if search field is empty, use the original list, else use filtered list
            if(constraint == null || constraint.isEmpty()) {
                filteredList.addAll(listDataFull)
            } else {
                val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
                for (item in listDataFull) {
                    val string = item.today!!.name + " " + item.today.county              // søker på navn og/eller fylke
                    if (string.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            listData = results?.values as List<ForestfireHolder>
            notifyDataSetChanged()
        }
    }



    // tilpasser dataen fra Forestfire-API slik at den passer til adapteren
    private fun convertForestfireData(response: List<ForestfireModel>): List<ForestfireHolder> {
        val list = mutableListOf<ForestfireHolder>()
        for (i in response[0].locations!!.indices){
            val mToday = response[0].locations!![i]
            val mTomorrow = response[1].locations!![i]
            val mTwodays = response[2].locations!![i]

            // Steder hvor det er grønt nivå alle 3 dagene vises ikke - tar unødvendig med plass/ressurser
            if ((mToday.danger_index!!.toIntOrNull() == null || mToday.danger_index.toIntOrNull()!! <= 3)
                && (mTomorrow.danger_index!!.toIntOrNull() == null || mTomorrow.danger_index.toIntOrNull()!! <= 3)
                && (mTwodays.danger_index!!.toIntOrNull() == null || mTwodays.danger_index.toIntOrNull()!! <= 3)) {
                continue
            }
            list.add(ForestfireHolder(mToday, mTomorrow, mTwodays))
        }
        return list
    }

    private fun getColor(i: String): String{
        return when(i.toIntOrNull()) {
            in 0..3 -> "#00ab2e"
            in 4..10 -> "#fff019"
            in 11..29 -> "#ff9f05"
            in 30..Int.MAX_VALUE -> "#ff4f19"
            else -> "#b8b8b8"
        }
    }
    private fun simplifyDangerlevel(i: String): String{
        return when(i.toIntOrNull()) {
            0 -> "0"
            in 1..3 -> "1"
            in 4..10 -> "2"
            in 11..29 -> "3"
            in 30..45 -> "4"
            in 46..Int.MAX_VALUE -> "5"
            else -> "-"
        }
    }






}