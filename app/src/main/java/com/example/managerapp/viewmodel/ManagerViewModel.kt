package com.example.managerapp.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.viewModelScope
import com.example.managerapp.ManagerApplication
import com.example.managerapp.models.Item
import com.example.managerapp.models.Transaction
import com.example.managerapp.repository.AuthRepository
import com.example.managerapp.repository.ManagerRepository
import com.example.managerapp.utils.NotificationHelper
import com.example.managerapp.utils.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.type.DateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    @ApplicationContext val app: Context,
    private val authRepository: AuthRepository,
    private val managerRepository: ManagerRepository
) : AndroidViewModel(app as Application) {

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

    private var _getTransactionHistoryResult =
        MutableStateFlow<Resource<List<Transaction>>>(Resource.StandBy())
    val getTransactionHistoryResult = _getTransactionHistoryResult.asStateFlow()


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
                            Log.d("TransactionFragment","Data updated successfully")
                            _addItemResult.value = Resource.Success(documentReference)
                            _addItemResult.value = Resource.StandBy()
                        }
                        .addOnFailureListener { e ->
                            Log.d("TransactionFragment","Unable to update data")
                            _addItemResult.value =
                                Resource.Error(e.message ?: "Unable to update item")
                            _addItemResult.value = Resource.StandBy()
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("TransactionFragment","Unable to add DataTr")
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
                    Log.d("UpdatingStock", "In success")
                    _updateItemStockResult.value = Resource.Success(Unit)
                    _updateItemStockResult.value = Resource.StandBy()
                }
                .addOnFailureListener { e ->
                    Log.d("UpdatingStock", e.message.toString())
                    _updateItemStockResult.value =
                        Resource.Error(e.message ?: "Unable to update stock")
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
                } catch (e: Exception) {
                    Log.d("HomeFragment", "in error")
                    _getItemResult.value =
                        Resource.Error(e.message ?: "Error occurred when getting all items")
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
                    _addTransactionResult.value =
                        Resource.Error(e.message ?: "Unable to add transaction")
                    _addTransactionResult.value = Resource.StandBy()
                }
        }
    }

    fun getTransactionHistory(userId: String, startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _getTransactionHistoryResult.value = Resource.Loading()
            managerRepository.getTransactionHistory(userId, startDate, endDate)
                .collect { transactions ->
                    try {
                        val modifiedTransactions = transactions.sortedBy { it.transaction_date_time }
                        _getTransactionHistoryResult.value = Resource.Success(modifiedTransactions)
                        delay(500)
                        _getTransactionHistoryResult.value = Resource.StandBy()
                    } catch (e: Exception) {
                        _getTransactionHistoryResult.value =
                            Resource.Error(e.message ?: "Unable to get transactions")
                        delay(500)
                        _getTransactionHistoryResult.value = Resource.StandBy()
                    }
                }
        }
    }

    fun generateTransactionPdf(transactions: List<Transaction>) {
        // Create a new PDF document
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Set up text appearance
        paint.color = Color.BLACK
        paint.textSize = 16f

        // Add Header
        val headerText = "Transaction History"
        paint.textSize = 24f
        canvas.drawText(headerText, 200f, 50f, paint)

        // Draw table header
        paint.textSize = 18f
        paint.color = Color.DKGRAY
        val headers = listOf("Item", "Type","Quantity","Date")
        var startX = 50f
        val startY = 100f
        val rowHeight = 40f
        val columnWidths = listOf(125f, 125f, 125f, 125f)

        headers.forEachIndexed { index, header ->
            canvas.drawText(header, startX, startY, paint)
            startX += columnWidths[index]
        }

        // Draw table rows with transaction data
        paint.textSize = 16f
        paint.color = Color.BLACK
        var currentY = startY + rowHeight
        transactions.forEach { transaction ->
            startX = 50f
            canvas.drawText(transaction.item_name!!, startX, currentY, paint)
            startX += columnWidths[0]
            canvas.drawText(transaction.transaction_type!!, startX, currentY, paint)
            startX += columnWidths[1]
            canvas.drawText(transaction.transaction_units.toString(), startX, currentY, paint)
            startX += columnWidths[2]
            canvas.drawText(epochToDateString(transaction.transaction_date_time.toString().toLong()), startX, currentY, paint)
            currentY += rowHeight
        }

        pdfDocument.finishPage(page)


        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val formattedDate = dateFormat.format(Calendar.getInstance().time)
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

        val file = File(directoryPath, "TransactionHistory_$formattedDate.pdf")


        try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            Log.d("HistoryFragment","Pdf downloaded, ${file.path}")

            val notificationHelper = NotificationHelper(app.applicationContext,"pdf_channel","Download PDF")
            notificationHelper.showDownloadNotification(file.path,"PDF Downloaded","PDF has been downloaded to")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("HistoryFragment","Pdf error $e")
        } finally {
            pdfDocument.close()
            Log.d("HistoryFragment","Pdf close")
        }
    }

    fun generateInventoryPdf(headerString:String,items: List<Item>,fileName:String) {
        // Create a new PDF document
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Set up text appearance
        paint.color = Color.BLACK
        paint.textSize = 16f

        // Add Header
        paint.textSize = 24f
        canvas.drawText(headerString, 200f, 50f, paint)

        // Draw table header
        paint.textSize = 18f
        paint.color = Color.DKGRAY
        val headers = listOf("Item","Stock","Price","Quantity")
        var startX = 50f
        val startY = 100f
        val rowHeight = 40f
        val columnWidths = listOf(125f, 125f, 125f, 125f)

        headers.forEachIndexed { index, header ->
            canvas.drawText(header, startX, startY, paint)
            startX += columnWidths[index]
        }

        // Draw table rows with transaction data
        paint.textSize = 16f
        paint.color = Color.BLACK
        var currentY = startY + rowHeight
        items.forEach { item ->
            startX = 50f
            canvas.drawText(item.item_name!!, startX, currentY, paint)
            startX += columnWidths[0]
            canvas.drawText(item.item_stock.toString(), startX, currentY, paint)
            startX += columnWidths[1]
            canvas.drawText(item.item_cost.toString(), startX, currentY, paint)
            startX += columnWidths[2]
            canvas.drawText(item.min_quantity.toString(), startX, currentY, paint)
            currentY += rowHeight
        }

        pdfDocument.finishPage(page)


        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val formattedDate = dateFormat.format(Calendar.getInstance().time)
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

        val file = File(directoryPath, "${fileName}_$formattedDate.pdf")


        try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            Log.d("Downloading File","Pdf downloaded, ${file.path}")

            val notificationHelper = NotificationHelper(app.applicationContext,"pdf_channel","Download PDF")
            notificationHelper.showDownloadNotification(file.path,"PDF Downloaded","PDF has been downloaded to")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("Downloading File","Pdf error $e")
        } finally {
            pdfDocument.close()
            Log.d("Downloading File","Pdf close")
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

    fun convertTimeToDate(time: Long): String {
        val ist = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        ist.timeInMillis = time
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return format.format(ist.time)
    }

    fun epochToDateString(epochSec: Long): String {
        val date = Date(epochSec*1000)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Customize the format as needed
        return format.format(date)
    }


}