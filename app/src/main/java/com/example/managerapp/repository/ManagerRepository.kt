package com.example.managerapp.repository

import android.util.Log
import com.example.managerapp.models.Item
import com.example.managerapp.models.Transaction
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow


class ManagerRepository {

    private val firebaseDb = FirebaseFirestore.getInstance()

    fun addItem(userId: String,item: Item): Task<DocumentReference> {
        return firebaseDb
            .collection("users")
            .document(userId)
            .collection("items")
            .add(item)
    }

    fun updateItem(userId: String, itemId: String): Task<Void> {
        return firebaseDb
            .collection("users")
            .document(userId)
            .collection("items")
            .document(itemId)
            .update(mapOf(Pair("item_id", itemId)))
    }

    fun updateItemStock(userId: String, itemId: String, stock: Int): Task<Void> {
        return firebaseDb
            .collection("users")
            .document(userId)
            .collection("items")
            .document(itemId)
            .update(mapOf("item_stock" to stock))
    }

    fun addTransaction(userId:String,transaction: Transaction):Task<DocumentReference>{
        return firebaseDb
            .collection("users")
            .document(userId)
            .collection("transactions")
            .add(transaction)
    }

    fun updateTransaction(userId: String,transId:String):Task<Void>{
        return firebaseDb
            .collection("users")
            .document(userId)
            .collection("items")
            .document(transId)
            .update(mapOf(Pair("transaction_id", transId)))
    }

    fun getAllItems(userId:String): Flow<List<Item>> = callbackFlow {
         val listenerItems = firebaseDb
            .collection("users")
            .document(userId)
            .collection("items")
            .addSnapshotListener { querySnapshot, e ->
                if(e!=null){
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                querySnapshot?.let { data ->
                    val items = data.toObjects(Item::class.java)
                    Log.d("HomeFragment",items.toString())
                    trySend(items)
                }
            }
         awaitClose{ listenerItems.remove() }
    }

    fun getAllTransactions(userId:String,startDate:String,endDate:String):Task<QuerySnapshot>{
        return firebaseDb
            .collection("users")
            .document(userId)
            .collection("transactions")
            .whereGreaterThanOrEqualTo("transaction_date_time",startDate)
            .whereLessThanOrEqualTo("transaction_date_time",endDate)
            .get()

    }


}