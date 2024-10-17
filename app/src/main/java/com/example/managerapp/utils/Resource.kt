package com.example.managerapp.utils

sealed class Resource<T>(
    val data:T?=null,
    val message:String?=null
){
    class Success<T>(data:T): Resource<T>(data)
    class Error<T>(message:String,data: T?=null): Resource<T>(data,message)
    class Loading<T>: Resource<T>()
    class StandBy<T>: Resource<T>()
}

//sealed class Resource<out T> {
//    data class Success<out T>(val data: T) : Resource<T>()
//    data class Error(val message: String) : Resource<Nothing>()
//    object Loading : Resource<Nothing>()
//}
