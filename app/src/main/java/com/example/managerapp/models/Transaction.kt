package com.example.managerapp.models

data class Transaction(
    val transaction_id:String?=null,
    val item_cost: Int?=null,
    val item_name: String?=null,
    val transaction_type:String?=null,
    val transaction_units:Int?=null,
    val final_stock:Int?=null,
    val transaction_date_time:String?=null
)
