package com.example.managerapp.models

import androidx.resourceinspection.annotation.Attribute.IntMap

data class Item(
    val item_id: String? = null,
    val item_name: String? = null,
    val item_cost: Int? = null,
    var item_stock: Int? = null,
    val min_quantity: Int? = null
)
