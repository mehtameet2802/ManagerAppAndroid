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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.managerapp.models.Item
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel

@Composable
fun AddItemScreen(viewModel: ManagerViewModel) {
    var itemName by rememberSaveable { mutableStateOf("") }
    var itemNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var itemCost by rememberSaveable { mutableStateOf("") }
    var itemCostError by rememberSaveable { mutableStateOf<String?>(null) }
    var minQuantity by rememberSaveable { mutableStateOf("") }
    var minQuantityError by rememberSaveable { mutableStateOf<String?>(null) }
    var itemStock by rememberSaveable { mutableStateOf("") }
    var itemStockError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current


    // Clear all fields
    fun clearFields() {
        println("Clearing fields...")
        itemName = ""
        itemNameError = null
        itemCost = ""
        itemCostError = null
        minQuantity = ""
        minQuantityError = null
        itemStock = ""
        itemStockError = null
    }


    fun saveItem() {
        itemNameError = if (itemName.isEmpty())
            "Enter Item Name"
        else
            null
        itemCostError = if (itemCost.isEmpty())
            "Enter Item Cost"
        else
            null
        minQuantityError = if (minQuantity.isEmpty())
            "Enter min quantity"
        else
            null
        itemStockError = if (itemStock.isEmpty())
            "Enter Item Stock"
        else
            null
        if(itemName.isNotEmpty() && itemCost.isNotEmpty() && minQuantity.isNotEmpty() && itemStock.isNotEmpty()){
            user?.let {
                viewModel.addItem(
                    it.uid,
                    Item(
                        null,
                        itemName,
                        itemCost.toInt(),
                        itemStock.toInt(),
                        minQuantity.toInt()
                    )
                )
            }
        }
    }


    LaunchedEffect(viewModel.addItemResult) {
        viewModel.addItemResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("unable to add new item ========= ")
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("new item added successfully ========= " + resource.data)
                    Toast.makeText(context, "Item Added", Toast.LENGTH_LONG).show()
                    isLoading = false
                    clearFields()
                }
            }

        }
    }



    AddItemScreenDesign(
        itemName = itemName,
        onItemNameChange = {
            itemName = it
        },
        itemNameError = itemNameError,
        itemCost = itemCost,
        onItemCostChange = {
            itemCost = it
        },
        itemCostError = itemCostError,
        minQuantity = minQuantity,
        onMinQuantityChange = {
            minQuantity = it
        },
        minQuantityError = minQuantityError,
        itemStock = itemStock,
        onItemStockChange = {
            itemStock = it
        },
        itemStockError = itemStockError,
        onSaveItem = {
            saveItem()
        },
        isLoading = isLoading,
    )
}


@Composable
fun AddItemScreenDesign(
    itemName: String,
    onItemNameChange: (String) -> Unit,
    itemNameError: String?,
    itemCost: String,
    onItemCostChange: (String) -> Unit,
    itemCostError: String?,
    minQuantity: String,
    onMinQuantityChange: (String) -> Unit,
    minQuantityError: String?,
    itemStock: String,
    onItemStockChange: (String) -> Unit,
    itemStockError: String?,
    onSaveItem: () -> Unit,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = itemName,
                onValueChange = onItemNameChange,
                isError = itemNameError != null,
                label = { Text("Item Name") },
                placeholder = {
                    Text("Please enter new Item Name")
                },
                supportingText = {
                    if (itemNameError != null) {
                        Text(itemNameError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = itemCost,
                onValueChange = onItemCostChange,
                isError = itemCostError != null,
                label = { Text("Item Cost") },
                placeholder = {
                    Text("Please enter item Cost")
                },
                supportingText = {
                    if (itemCostError != null) {
                        Text(itemCostError)
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


            OutlinedTextField(
                value = minQuantity,
                onValueChange = onMinQuantityChange,
                isError = minQuantityError != null,
                label = { Text("Min Quantity") },
                placeholder = {
                    Text("Please enter min quantity")
                },
                supportingText = {
                    if (minQuantityError != null) {
                        Text(minQuantityError)
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

            OutlinedTextField(
                value = itemStock,
                onValueChange = onItemStockChange,
                isError = itemStockError != null,
                label = { Text("Item Stock") },
                placeholder = {
                    Text("Please enter item Stock")
                },
                supportingText = {
                    if (itemStockError != null) {
                        Text(itemStockError)
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
                onClick = onSaveItem,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Save")
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
fun AddItemPreview() {
    AddItemScreenDesign(
        itemName = "",
        onItemNameChange = {},
        itemNameError = "",
        itemCost = "",
        onItemCostChange = {},
        itemCostError = "",
        minQuantity = "",
        onMinQuantityChange = {},
        minQuantityError = "",
        itemStock = "",
        onItemStockChange = {},
        itemStockError = "",
        onSaveItem = {},
        isLoading = false,
    )
}