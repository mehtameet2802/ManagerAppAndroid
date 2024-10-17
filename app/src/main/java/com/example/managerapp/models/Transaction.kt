package com.example.managerapp.models

data class Transaction(
    val transaction_id:String,
    val item_cost: Int,
    val item_name: String,
    val transaction_type:String,
    val transaction_units:Int,
    val final_stock:Int,
    val transaction_date_time:String
)
