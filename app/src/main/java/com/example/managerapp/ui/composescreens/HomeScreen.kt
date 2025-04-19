package com.example.managerapp.ui.composescreens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.managerapp.models.Item
import com.example.managerapp.models.TopBarActions
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel

@Composable
fun HomeScreen(
    viewModel: ManagerViewModel,
    navController: NavController,
    setTopBarActions: (TopBarActions) -> Unit,
) {

    var sum by rememberSaveable { mutableIntStateOf(0) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var allItems by rememberSaveable { mutableStateOf<List<Item>>(emptyList()) }
    var visibleItems by rememberSaveable { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val itemResult by viewModel.getItemResult.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    fun calculateSum(items: List<Item>?) {
        sum = 0
        if (items != null) {
            for (item in items) {
                sum += item.item_stock!! * item.item_cost!!
            }
        }
    }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            viewModel.getAllItems(uid)
        }
    }

    LaunchedEffect(Unit) {
        setTopBarActions(
            TopBarActions(
                onAdd = {
                    navController.navigate("addItem")
                },
                onDownload = {
                    if (allItems.isEmpty())
                        Toast.makeText(context, "No items found", Toast.LENGTH_LONG).show()
                    else {
                        Toast.makeText(context, "Downloading File", Toast.LENGTH_LONG).show()
                        viewModel.generateInventoryPdf(
                            "Current Inventory",
                            allItems,
                            "CurrentInventory"
                        )
                    }
                }
            )
        )
    }

    when (itemResult) {
        is Resource.Error -> {
            isLoading = false
            Toast.makeText(context, itemResult.message, Toast.LENGTH_LONG).show()
        }

        is Resource.Loading -> isLoading = true
        is Resource.Success -> {
            isLoading = false
            allItems = itemResult.data ?: emptyList()
            visibleItems = allItems
            calculateSum(itemResult.data)
        }

        else -> Unit
    }

    LaunchedEffect(viewModel.updateItemStockResult) {
        viewModel.updateItemStockResult.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Re-fetch items after updating stock
                    user?.uid?.let { uid -> viewModel.getAllItems(uid) }
                }

                is Resource.Error -> {
                    isLoading = false
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }

                is Resource.Loading -> isLoading = true
                is Resource.StandBy -> isLoading = false
            }
        }
    }

    HomeScreenDesign(
        allItems = visibleItems,
        increaseStock = {
            viewModel.updateItemStock(user!!.uid, it.item_id!!, it.item_stock!! + 1)
        },
        decreaseStock = {
            viewModel.updateItemStock(user!!.uid, it.item_id!!, it.item_stock!! - 1)
        },
        totalAmount = sum,
        searchText = searchText,
        onSearch = {
            searchText = it
            visibleItems = if (it.isEmpty()) {
                allItems
            } else {
                allItems.filter { item ->
                    item.item_name!!.contains(it, ignoreCase = true)
                }
            }
        },
        isLoading = isLoading
    )

}

@Composable
fun ItemCard(
    item: Item,
    increaseStock: (Item) -> Unit,
    decreaseStock: (Item) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.item_name!!,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 5.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        decreaseStock(item)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Add"
                    )
                }

                Text(
                    text = item.item_stock.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                IconButton(
                    onClick = {
                        increaseStock(item)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }

        }
    }
}

@Composable
fun HomeScreenDesign(
    allItems: List<Item>,
    increaseStock: (Item) -> Unit,
    decreaseStock: (Item) -> Unit,
    totalAmount: Int,
    searchText: String,
    onSearch: (String) -> Unit,
    isLoading: Boolean,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearch,
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            ) {
                items(
                    count = allItems.count(),
                ) { item ->
                    ItemCard(
                        item = allItems[item],
                        increaseStock = increaseStock,
                        decreaseStock = decreaseStock
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = Color.Black
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Value : ",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = totalAmount.toString(),
                    modifier = Modifier.padding(10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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
fun HomeScreenPreview() {
    HomeScreenDesign(
        allItems = emptyList(),
        increaseStock = {},
        decreaseStock = {},
        totalAmount = 0,
        searchText = "",
        onSearch = {},
        isLoading = false,
    )
}