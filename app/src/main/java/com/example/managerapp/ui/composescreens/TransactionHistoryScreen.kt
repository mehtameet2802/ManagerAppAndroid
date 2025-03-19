package com.example.managerapp.ui.composescreens

import android.util.Log
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel


@Composable
fun TransactionHistoryScreen(viewModel: ManagerViewModel) {

    var startDate by rememberSaveable { mutableStateOf("") }
    var startDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var endDate by rememberSaveable { mutableStateOf("") }
    var endDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    var isDatePickerVisible by rememberSaveable { mutableStateOf(false) }
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
        onShowDatePicker = {
            isDatePickerVisible = true
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
        selectDateRange = {

            calenderStartDate = (it.first?.div(1000)).toString()
            calenderEndDate = (it.second?.div(1000)).toString()

            startDate = it.first?.let { it1 -> viewModel.convertTimeToDate(it1) }.toString()
            endDate = it.second?.let { it1 -> viewModel.convertTimeToDate(it1) }.toString()
            Log.d("HistoryFragment", it.toString())

        },
        onDismissDatePicker = {
            isDatePickerVisible = false
        },
        isDatePickerVisible = isDatePickerVisible,
        isLoading = isLoading,
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateRangeSelected(
                            Pair(
                                dateRangePickerState.selectedStartDateMillis,
                                dateRangePickerState.selectedEndDateMillis
                            )
                        )
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Select Dates"
                    )
                },
                showModeToggle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            )
        }
    }
}


@Composable
fun TransactionHistoryDesign(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    startDateError: String?,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    endDateError: String?,
    onShowDatePicker: () -> Unit,
    onDownload: () -> Unit,
    selectDateRange: (Pair<Long?, Long?>) -> Unit,
    onDismissDatePicker: () -> Unit,
    isDatePickerVisible: Boolean,
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
                onClick = onShowDatePicker,
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

        if (isDatePickerVisible) {
            DateRangePickerModal(
                onDateRangeSelected = selectDateRange,
                onDismiss = onDismissDatePicker
            )
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
        onShowDatePicker = {},
        onDownload = {},
        selectDateRange = {},
        onDismissDatePicker = {},
        isDatePickerVisible = true,
        isLoading = false
    )
}