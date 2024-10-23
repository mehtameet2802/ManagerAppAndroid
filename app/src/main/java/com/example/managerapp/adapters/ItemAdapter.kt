package com.example.managerapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.managerapp.R
import com.example.managerapp.models.Item
import java.util.logging.LogRecord

class ItemAdapter(
    itemList:List<Item>,
    private val listener: OnItemInteractionListener
):RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(),Filterable {

    private var originalList:List<Item> = itemList.toList()
    private var filteredList:List<Item> = itemList.toList()

    inner class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bind(currentItem:Item) {
            val tvItem = itemView.findViewById<TextView>(R.id.rvItem)
            val btnInc = itemView.findViewById<ImageButton>(R.id.rvIncBtn)
            val decBtn = itemView.findViewById<ImageButton>(R.id.rvDecBtn)
            val tvAmount = itemView.findViewById<TextView>(R.id.rvItemQnt)

            itemView.apply {
                tvItem.text = currentItem.item_name
                tvAmount.text = currentItem.item_stock.toString()

                if (currentItem.min_quantity!! > currentItem.item_stock!!)
                    tvAmount.setTextColor(Color.RED)
                else
                    tvAmount.setTextColor(Color.BLACK)

                var updateStock = currentItem.item_stock!!
                btnInc.setOnClickListener {
                    updateStock+=1
                    listener.onUpdateStock(currentItem,updateStock)
                }

                decBtn.setOnClickListener {
                    updateStock-=1
                    if(updateStock<=0)
                        updateStock = 0
                    listener.onUpdateStock(currentItem,updateStock)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase()?:""
                filteredList = if(query.isEmpty()){
                    originalList
                } else{
                    originalList.filter { item ->
                        item.item_name?.lowercase()?.contains(query) == true
                    }
                }

                return FilterResults().apply {
                    values = filteredList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredList = results?.values as List<Item>
                notifyDataSetChanged()
            }
        }
    }



    fun updateItems(newItems:List<Item>){
        originalList = newItems.toList()
        filteredList = newItems.toList()
        notifyDataSetChanged()
    }

}