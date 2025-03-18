package com.example.managerapp.ui.composescreens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.managerapp.models.Item
import com.example.managerapp.models.Transaction
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel


@Composable
fun TransactionScreen(viewModel: ManagerViewModel) {

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var quantity by rememberSaveable { mutableStateOf("") }
    var item by rememberSaveable { mutableStateOf("Choose Item") }
    var transaction by rememberSaveable { mutableStateOf("Choose Transaction") }
    var quantityError by rememberSaveable { mutableStateOf<String?>(null) }
    var itemError by rememberSaveable { mutableStateOf<String?>(null) }
    var transactionError by rememberSaveable { mutableStateOf<String?>(null) }
    var itemDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var transactionDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    var items by rememberSaveable { mutableStateOf<List<Item>>(emptyList()) }

    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Define item options
    val itemOptions = remember(items) {
        val list = ArrayList<String>()
        list.add("Choose Item")
        list.addAll(items.map { it.item_name ?: "" })
        list
    }

    val transactionOptions = remember { listOf("Choose Transaction", "Buy", "Sell") }


    fun clearFields() {
        item = "Choose Item"
        transaction = "Choose Transaction"
        quantity = ""
        quantityError = null
        itemError = null
        transactionError = null
    }

    fun performTransaction() {

        if (item.isEmpty() || item == "Choose Item")
            itemError = "Select an item"
        else if (transaction.isEmpty() || transaction == "Choose Transaction") {
            println("Here transaction error")
            itemError = null
            transactionError = "Select transaction"
        } else if (quantity.isEmpty()) {
            println("here quantity error")
            itemError = null
            transactionError = null
            quantityError = "Enter quantity"
        } else {
            println("Here entering transact btn")
            quantityError = null
            itemError = null
            transactionError = null
            val currentItem = items.filter { it.item_name == item }[0]
            var newStock = currentItem.item_stock!!
            if (transaction == "Buy") {
                newStock += quantity.toInt()
            } else if (transaction == "Sell") {
                newStock -= quantity.toInt()
            }
            println("Calling all the Viewmodel")
            user?.let { viewModel.updateItemStock(it.uid, currentItem.item_id!!, newStock) }
            user?.let {
                viewModel.addTransaction(
                    it.uid,
                    Transaction(
                        "",
                        currentItem.item_cost!!,
                        currentItem.item_name!!,
                        transaction,
                        quantity.toInt(),
                        newStock,
                        (System.currentTimeMillis() / 1000).toString()
                    )
                )
            }

        }
    }

    // Load items when screen is created
    LaunchedEffect(Unit) {
        user?.let {
            viewModel.getAllItems(it.uid)
        }
    }

    LaunchedEffect(viewModel.updateItemStockResult) {
        viewModel.updateItemStockResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("update stock error ========= ")
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("stock updated successfully ========= ")
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(viewModel.addTransactionResult) {
        viewModel.addTransactionResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("update stock error ========= ")
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("stock updated successfully ========= ")
                    isLoading = false
                    Toast.makeText(
                        context,
                        "Transaction successful",
                        Toast.LENGTH_LONG
                    ).show()
                    clearFields()
                }
            }

        }
    }

    LaunchedEffect(viewModel.getItemResult) {
        viewModel.getItemResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("unable to get items ========= ")
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("got items successfully ========= " + resource.data)
                    isLoading = false
                    items = resource.data ?: emptyList()
                }
            }
        }
    }

    TransactionScreenDesign(
        quantity = quantity,
        onQuantityChange = {
            quantity = it
        },
        quantityError = quantityError,
        itemOptions = itemOptions.toList(),
        itemSelected = item,
        itemError = itemError,
        onItemSelected = {
            item = it
        },
        itemDropdownExpanded = itemDropdownExpanded,
        onItemDropdownExpandedChange = {
            itemDropdownExpanded = it
        },
        transactionOptions = transactionOptions,
        transactionSelected = transaction,
        transactionError = transactionError,
        onTransactionSelected = {
            transaction = it
        },
        transactionDropdownExpanded = transactionDropdownExpanded,
        onTransactionDropdownExpandedChange = {
            transactionDropdownExpanded = it
        },
        onTransact = { performTransaction() },
        isLoading = isLoading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenDesign(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    quantityError: String?,
    itemOptions: List<String>,
    itemSelected: String,
    itemError: String?,
    onItemSelected: (String) -> Unit,
    onItemDropdownExpandedChange: (Boolean) -> Unit,
    itemDropdownExpanded: Boolean,
    transactionOptions: List<String>,
    transactionSelected: String,
    transactionError: String?,
    onTransactionSelected: (String) -> Unit,
    transactionDropdownExpanded: Boolean,
    onTransactionDropdownExpandedChange: (Boolean) -> Unit,
    onTransact: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {

            ExposedDropdownMenuBox(
                expanded = itemDropdownExpanded,
                onExpandedChange = { onItemDropdownExpandedChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = itemSelected,
                    onValueChange = { onItemSelected(it) },
                    readOnly = true,
                    label = { Text("Select Item") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemDropdownExpanded)
                    },
                    isError = itemError != null,
                    supportingText = {
                        if (itemError != null) {
                            Text(itemError)
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = itemDropdownExpanded,
                    onDismissRequest = { onItemDropdownExpandedChange(false) }
                ) {
                    itemOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                onItemSelected(item)
                                onItemDropdownExpandedChange(false)
                            }
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            ExposedDropdownMenuBox(
                expanded = transactionDropdownExpanded,
                onExpandedChange = { onTransactionDropdownExpandedChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = transactionSelected,
                    onValueChange = { onTransactionSelected(it) },
                    readOnly = true,
                    label = { Text("Select Transaction") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = transactionDropdownExpanded)
                    },
                    isError = transactionError != null,
                    supportingText = {
                        if (transactionError != null) {
                            Text(transactionError)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = transactionDropdownExpanded,
                    onDismissRequest = { onTransactionDropdownExpandedChange(false) }
                ) {
                    transactionOptions.forEach { transaction ->
                        DropdownMenuItem(
                            text = { Text(transaction) },
                            onClick = {
                                onTransactionSelected(transaction)
                                onTransactionDropdownExpandedChange(false)
                            }
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = onQuantityChange,
                isError = quantityError != null,
                label = { Text("Quantity") },
                placeholder = {
                    Text("Please enter quantity")
                },
                supportingText = {
                    if (quantityError != null) {
                        Text(quantityError)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, // Allow number input only
                    imeAction = ImeAction.Done // Done action on keyboard
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = onTransact,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Transact")
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    TransactionScreenDesign(
        quantity = "10",
        onQuantityChange = {},
        quantityError = null,
        itemOptions = listOf("Choose Item", "Item 1", "Item 2", "Item 3"),
        itemSelected = "Item 1",
        itemError = null,
        onItemSelected = {},
        onItemDropdownExpandedChange = {},
        itemDropdownExpanded = false,
        transactionOptions = listOf("Choose Transaction", "Buy", "Sell"),
        transactionSelected = "Buy",
        transactionError = null,
        onTransactionSelected = {},
        transactionDropdownExpanded = false,
        onTransactionDropdownExpandedChange = {},
        onTransact = {},
        isLoading = false
    )
}

