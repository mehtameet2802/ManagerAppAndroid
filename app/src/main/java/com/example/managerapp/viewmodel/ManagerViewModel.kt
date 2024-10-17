package com.example.managerapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.viewModelScope
import com.example.managerapp.ManagerApplication
import com.example.managerapp.models.Item
import com.example.managerapp.models.Transaction
import com.example.managerapp.repository.AuthRepository
import com.example.managerapp.repository.ManagerRepository
import com.example.managerapp.utils.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManagerViewModel(
    val app: Application,
    private val authRepository: AuthRepository,
    private val managerRepository: ManagerRepository
) : AndroidViewModel(app) {

    private var _addItemResult = MutableStateFlow<Resource<DocumentReference>>(Resource.StandBy())
    val addItemResult = _addItemResult.asStateFlow()

//    private var _updateItemResult = MutableStateFlow<Resource<Void>>(Resource.StandBy())
//    val updateItemResult = _updateItemResult.asStateFlow()

    private var _updateItemStockResult = MutableStateFlow<Resource<Unit>>(Resource.StandBy())
    val updateItemStockResult = _updateItemStockResult.asStateFlow()

    private var _getItemResult = MutableStateFlow<Resource<List<Item>>>(Resource.StandBy())
    val getItemResult = _getItemResult.asStateFlow()

//    private var _updateTransactionResult = MutableStateFlow<Resource<Void>>(Resource.StandBy())
//    val updateTransactionResult = _updateTransactionResult.asStateFlow()

    private var _addTransactionResult =
        MutableStateFlow<Resource<DocumentReference>>(Resource.StandBy())
    val addTransactionResult = _addTransactionResult.asStateFlow()

    private var _getTransactionResult =
        MutableStateFlow<Resource<QuerySnapshot>>(Resource.StandBy())
    val getTransactionResult = _getTransactionResult.asStateFlow()


    fun logout() {
        authRepository.logout()
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

    fun addItem(userId: String, item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            _addItemResult.value = Resource.StandBy()
            managerRepository.addItem(userId, item)
                .addOnSuccessListener { documentReference ->
                    managerRepository.updateItem(userId, documentReference.id)
                        .addOnSuccessListener {
                            _addItemResult.value = Resource.Success(documentReference)
                            _addItemResult.value = Resource.StandBy()
                        }
                        .addOnFailureListener { e ->
                            _addItemResult.value =
                                Resource.Error(e.message ?: "Unable to update item")
                            _addItemResult.value = Resource.StandBy()
                        }
                }
                .addOnFailureListener { e ->
                    _addItemResult.value = Resource.Error(e.message ?: "Unable to add Item")
                    _addItemResult.value = Resource.StandBy()
                }
        }
    }

    fun updateItemStock(userId: String, itemId: String, stock: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateItemStockResult.value = Resource.StandBy()
            managerRepository.updateItemStock(userId, itemId, stock)
                .addOnSuccessListener {
                    Log.d("UpdatingStock","In success")
                    _updateItemStockResult.value = Resource.Success(Unit)
                    _updateItemStockResult.value = Resource.StandBy()
                }
                .addOnFailureListener { e ->
                    Log.d("UpdatingStock",e.message.toString())
                    _updateItemStockResult.value = Resource.Error(e.message ?: "Unable to update stock")
                    _updateItemStockResult.value = Resource.StandBy()
                }
        }
    }

    fun getAllItems(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _getItemResult.value = Resource.Loading()
            managerRepository.getAllItems(userId).collect { items ->
//                Log.d("HomeFragment", items.toString())
                try {
                    Log.d("HomeFragment", "in success")
                    _getItemResult.value = Resource.Success(items)
                    delay(500)
                    _getItemResult.value = Resource.StandBy()
                } catch (e:Exception) {
                    Log.d("HomeFragment", "in error")
                    _getItemResult.value = Resource.Error(e.message?:"Error occurrec when getting all items")
                    delay(500)
                    _getItemResult.value = Resource.StandBy()
                }
            }
        }
    }

    fun addTransaction(userId: String, transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            _addTransactionResult.value = Resource.StandBy()
            managerRepository.addTransaction(userId, transaction)
                .addOnSuccessListener { documentReference ->
                    managerRepository.updateTransaction(userId, documentReference.id)
                        .addOnSuccessListener {
                            _addTransactionResult.value = Resource.Success(documentReference)
                            _addTransactionResult.value = Resource.StandBy()
                        }
                        .addOnFailureListener { e ->
                            _addTransactionResult.value =
                                Resource.Error(e.message ?: "Unable to update transaction")
                            _addTransactionResult.value = Resource.StandBy()
                        }
                }
                .addOnFailureListener { e ->
                    _addTransactionResult.value = Resource.Error(e.message ?: "Unable to add transaction")
                    _addTransactionResult.value = Resource.StandBy()
                }
        }
    }

    fun getAllTransaction(userId: String, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _getTransactionResult.value = Resource.Loading()
            managerRepository.getAllTransactions(userId, startDate, endDate)
                .addOnSuccessListener { querySnapshot ->
                    _getTransactionResult.value = Resource.Success(querySnapshot)
                    _getTransactionResult.value = Resource.StandBy()
                }
                .addOnFailureListener { e ->
                    _getTransactionResult.value =
                        Resource.Error(e.message ?: "Could not get the items")
                    _getTransactionResult.value = Resource.StandBy()
                }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<ManagerApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}