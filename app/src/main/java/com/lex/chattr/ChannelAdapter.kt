package com.lex.chattr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChannelAdapter(
    private val dataSet: List<ChatChannel>,
    private val onItemClicked: (ChatChannel) -> Unit
) :
    RecyclerView.Adapter<ChannelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewChannelName: TextView

        init {
            textViewChannelName = view.findViewById(R.id.textview_channel_name)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.channel_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewChannelName.text = "#${dataSet[position].name}"
        viewHolder.textViewChannelName.setOnClickListener {
            onItemClicked(dataSet[position])
        }
    }

    override fun getItemCount() = dataSet.size
}

