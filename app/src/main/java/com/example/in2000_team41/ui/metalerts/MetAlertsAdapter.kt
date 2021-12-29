package com.example.in2000_team41.ui.metalerts


import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team41.R
import com.example.in2000_team41.api.metalerts.AlertModel
import kotlinx.android.synthetic.main.element_metalerts.view.*
import java.util.*
import kotlin.collections.ArrayList

class MetAlertsAdapter(
    private val listener: OnItemClickListener,
    private val userPositionAddress: String) : RecyclerView.Adapter<MetAlertsAdapter.ViewHolder>(), Filterable {

    var listData = listOf<AlertModel>()
    var listDataFull = listOf<AlertModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.element_metalerts,
            parent,
            false)
        return ViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listData[position]

        when (currentItem.info?.event?.toLowerCase(Locale.ROOT)) {
            "kuling" -> holder.image.setImageResource(R.drawable.weather_wind)
            "storm" -> holder.image.setImageResource(R.drawable.weather_storm)
            "sterk ising på skip" -> holder.image.setImageResource(R.drawable.symbol_cold)
            "snø" -> holder.image.setImageResource(R.drawable.weather_snow)
            "kraftig snøfokk" -> holder.image.setImageResource(R.drawable.weather_snow)
            "skogbrannfare" -> holder.image.setImageResource(R.drawable.img_forestfire)
            else -> holder.image.setImageResource(R.drawable.weather_warning)
        }
        when(currentItem.info?.parameter!!["awareness_level"]!!.split("; ")[1]) {
            "green" -> holder.warningImage.setImageResource(R.drawable.warning_orange)
            "yellow" -> holder.warningImage.setImageResource(R.drawable.warning_yellow)
            "orange" -> holder.warningImage.setImageResource(R.drawable.warning_orange)
            "red" -> holder.warningImage.setImageResource(R.drawable.warning_red)
            else -> holder.warningImage.setImageResource(R.drawable.warning_black)
        }
        holder.title.text = currentItem.info?.area?.areaDesc
        holder.title.paintFlags = holder.title.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        holder.distanceFromPostion.text = String.format("%.2f", currentItem.distance) + "km fra " + userPositionAddress

        holder.fareniva.text = "Farenivå: " + currentItem.info?.parameter!!["awareness_level"]!!.split("; ")[0]
    }

    override fun getItemCount() = listData.size

    fun updateAdapter(newList: List<AlertModel>) {
        listData = newList
        listDataFull = ArrayList(newList) // copy the list to use it independently
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val image: ImageView = itemView.metAlerts_image
        val warningImage: ImageView = itemView.warning_image
        val title: TextView = itemView.title_tv
        val fareniva: TextView = itemView.farenivaa_tv
        val distanceFromPostion: TextView = itemView.distanceFromPosition_tv

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            val alertClicked = listData[position]
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickMet(alertClicked)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClickMet(alertClicked: AlertModel)
    }

    //return a Filter which constrains data with a filtering pattern
    override fun getFilter(): Filter {
        return MetAlertsFilter(listDataFull, this)
    }


}