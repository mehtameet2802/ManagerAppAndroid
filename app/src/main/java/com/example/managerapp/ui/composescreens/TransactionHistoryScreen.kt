package com.example.managerapp.ui.composescreens

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.android.material.datepicker.MaterialDatePicker

// Helper function to get FragmentManager in Compose
@Composable
fun rememberFragmentManager(): FragmentManager {
    val context = LocalContext.current
    return remember {
        (context as AppCompatActivity).supportFragmentManager
    }
}

@Composable
fun TransactionHistoryScreen(viewModel: ManagerViewModel) {

    var startDate by rememberSaveable { mutableStateOf("") }
    var startDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var endDate by rememberSaveable { mutableStateOf("") }
    var endDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    var calenderStartDate by rememberSaveable { mutableStateOf("") }
    var calenderEndDate by rememberSaveable { mutableStateOf("") }

    val user by viewModel.currentUser.collectAsState()

    val context = LocalContext.current


    fun clearFields() {
        startDate = ""
        endDate = ""
    }

    LaunchedEffect(viewModel.getTransactionHistoryResult) {
        viewModel.getTransactionHistoryResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("error getting the transactions ========= ")
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("got transaction successfully ========= ")
                    isLoading = false
                    if (resource.data!!.isEmpty())
                        Toast.makeText(context, "No Transactions Found", Toast.LENGTH_LONG)
                            .show()
                    else {
                        Toast.makeText(
                            context,
                            "Downloading Transaction",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.generateTransactionPdf(resource.data)
                    }
                }
            }

        }
    }

    TransactionHistoryDesign(
        startDate = startDate,
        onStartDateChange = {
            startDate = it
        },
        startDateError = startDateError,
        endDate = endDate,
        onEndDateChange = {
            endDate = it
        },
        endDateError = endDateError,
        onSelectDates = {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

            picker.show(rememberFragmentManager(), "TransactionFragment")

            picker.addOnPositiveButtonClickListener {
                calenderStartDate = (it.first / 1000).toString()
                calenderEndDate = (it.second / 1000).toString()

                startDate = viewModel.convertTimeToDate(it.first)
                endDate = viewModel.convertTimeToDate(it.second)
                Log.d("HistoryFragment", it.toString())
            }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss()
            }
        },
        onDownload = {
            if (startDate.isEmpty() && endDate.isEmpty()) {
                Toast.makeText(context, "Select start and end date", Toast.LENGTH_LONG)
                    .show()
                startDateError = "Select Start Date"
                endDateError = "Select End Date"
            } else {
                startDateError = null
                endDateError = null
                user?.let {
                    viewModel.getTransactionHistory(
                        it.uid,
                        calenderStartDate,
                        calenderEndDate
                    )
                }
            }
        },
        isLoading = isLoading,
    )

}

@Composable
fun TransactionHistoryDesign(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    startDateError: String?,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    endDateError: String?,
    onSelectDates: @Composable () -> Unit,
    onDownload: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {

            OutlinedTextField(
                value = startDate,
                onValueChange = onStartDateChange,
                isError = startDateError != null,
                label = { Text("Start Date") },
                placeholder = {
                    Text("Please enter Start Date")
                },
                supportingText = {
                    if (startDateError != null) {
                        Text(startDateError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = endDate,
                onValueChange = onEndDateChange,
                isError = endDateError != null,
                label = { Text("End Date") },
                placeholder = {
                    Text("Please enter End Date")
                },
                supportingText = {
                    if (endDateError != null) {
                        Text(endDateError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onSelectDates },
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Select Dates")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onDownload,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Download")
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
fun TransactionHistoryPreview() {
    TransactionHistoryDesign(
        startDate = "",
        onStartDateChange = {},
        startDateError = "",
        endDate = "",
        onEndDateChange = {},
        endDateError = "",
        onSelectDates = {},
        onDownload = {},
        isLoading = false
    )
}