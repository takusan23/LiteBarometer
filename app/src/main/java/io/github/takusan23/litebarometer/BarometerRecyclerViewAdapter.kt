package io.github.takusan23.litebarometer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BarometerRecyclerViewAdapter(var list: ArrayList<ArrayList<String>>) :
    RecyclerView.Adapter<BarometerRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_barometer_layout, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.createAtTextView.text = item[1]
        holder.valueTextView.text = item[2] + " hPa"
        holder.valueFloatTextView.text = item[3] + " hPa"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var createAtTextView =
            itemView.findViewById<TextView>(R.id.adapter_barometer_create_at_textview)
        var valueTextView = itemView.findViewById<TextView>(R.id.adapter_barometer_value_textview)
        var valueFloatTextView =
            itemView.findViewById<TextView>(R.id.adapter_barometer_value_float_textview)
    }

}