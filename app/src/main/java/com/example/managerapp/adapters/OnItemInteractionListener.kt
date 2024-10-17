package com.example.managerapp.adapters

import com.example.managerapp.models.Item

interface OnItemInteractionListener {
    fun onUpdateStock(item:Item,newStock:Int)
}