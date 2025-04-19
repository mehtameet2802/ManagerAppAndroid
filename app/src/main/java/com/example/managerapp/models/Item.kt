package com.example.managerapp.models

import android.os.Parcelable
import androidx.resourceinspection.annotation.Attribute.IntMap
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val item_id: String? = null,
    val item_name: String? = null,
    val item_cost: Int? = null,
    var item_stock: Int? = null,
    val min_quantity: Int? = null
): Parcelable
