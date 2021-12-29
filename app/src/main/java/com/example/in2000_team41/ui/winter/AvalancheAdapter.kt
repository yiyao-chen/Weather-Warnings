package com.example.in2000_team41.ui.winter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team41.R
import com.example.in2000_team41.api.avalanche.AvalancheModel
import kotlinx.android.synthetic.main.element_avalanche.view.*
import java.util.*
import kotlin.collections.ArrayList

class AvalancheAdapter(
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<AvalancheAdapter.ViewHolder>() {

    var listData = listOf<AvalancheModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.element_avalanche,
            parent,
            false
        )
        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listData[position]
        holder.nameTextView.text = currentItem.Name
        holder.nameTextView.paintFlags = holder.nameTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.typeNameTextView.text = "Regionstype: " + currentItem.TypeName

        holder.image1.setBackgroundColor(Color.parseColor(getColor(currentItem.AvalancheWarningList[0].DangerLevel!!)))
        holder.image1.text = currentItem.AvalancheWarningList[0].DangerLevel!!
        holder.image2.setBackgroundColor(Color.parseColor(getColor(currentItem.AvalancheWarningList[1].DangerLevel!!)))
        holder.image2.text = currentItem.AvalancheWarningList[1].DangerLevel!!
        holder.image3.setBackgroundColor(Color.parseColor(getColor(currentItem.AvalancheWarningList[2].DangerLevel!!)))
        holder.image3.text = currentItem.AvalancheWarningList[2].DangerLevel!!
    }


    override fun getItemCount() = listData.size

    fun updateAdapter(newList: List<AvalancheModel>) {
        listData = newList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val nameTextView: TextView = itemView.nameRegion_tv
        val typeNameTextView: TextView = itemView.typeName_tv
        val image1: TextView = itemView.warning_day1_tv
        val image2: TextView = itemView.warning_day2_tv
        val image3: TextView = itemView.warning_day3_tv


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickAvalancheSummary(listData[position])
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClickAvalancheSummary(current: AvalancheModel)
    }




    private fun getColor(i: String): String{
        return when(i.toIntOrNull()) {
            in 0..1 -> "#00ab2e"
            2 -> "#fff019"
            3 -> "#ff9f05"
            4 -> "#ff4f19"
            5 -> "#000000"
            else -> "#b8b8b8"
        }
    }



}