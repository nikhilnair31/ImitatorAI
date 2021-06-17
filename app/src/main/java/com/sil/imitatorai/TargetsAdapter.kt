package com.sil.imitatorai

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_target.view.*
import java.util.HashMap

/**
 *
 */
class TargetsAdapter(
    private val items: MutableList<HashMap<String, String>>,
    val context: Context, val listner: (HashMap<String, String>) -> Unit
) : RecyclerView.Adapter<TargetsAdapter.ViewHolder>() {

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, ind: Int) {
        holder.setItem(items[ind])
        holder.targetName.text = items[ind]["targetname"]
        holder.replyRate.text = items[ind]["replydelay"]
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     *
     */
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_target, p0, false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val targetName = view.target_name_txt
        val replyRate = view.reply_delay_txt
        val trgLayout = view.target_layout

        fun setItem(item: HashMap<String, String>) {
            targetName?.text = item["targetname"]
            replyRate?.text = item["replydelay"]
            trgLayout.setOnClickListener {
                listner(item)
            }
        }
    }

}