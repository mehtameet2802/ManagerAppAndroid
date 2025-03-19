package com.example.managerapp.ui.composescreens

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.managerapp.viewmodel.ManagerViewModel

@Composable
fun UploadFileScreen(viewModel: ManagerViewModel) {

    var fileName by rememberSaveable { mutableStateOf("") }
    var fileType by rememberSaveable { mutableStateOf("File Format") }
    var fileNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var fileTypeError by rememberSaveable { mutableStateOf<String?>(null) }
    var fileTypeDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    fun clearFields() {
        fileType = "File Format"
        fileName = ""
        fileNameError = null
        fileTypeError = null
//        fileUri = null
//        csvData = mutableListOf()
    }

    UploadFileDesign(
        fileName = fileName,
        fileNameError = fileNameError,
        onSelectFile = {},
        fileType = fileType,
        fileTypeOptions = listOf("File Format", "csv"),
        fileTypeError = fileTypeError,
        onFileTypeSelected = {},
        fileTypeDropdownExpanded = fileTypeDropdownExpanded,
        onFileTypeDropdownExpandedChange = {
            fileTypeDropdownExpanded = it
        },
        onUploadFile = {},
        isLoading = isLoading
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFileDesign(
    fileName: String,
    fileNameError: String?,
    onSelectFile: () -> Unit,
    fileType: String,
    fileTypeOptions: List<String>,
    fileTypeError: String?,
    onFileTypeSelected: (String) -> Unit,
    fileTypeDropdownExpanded: Boolean,
    onFileTypeDropdownExpandedChange: (Boolean) -> Unit,
    onUploadFile: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {

            ExposedDropdownMenuBox(
                expanded = fileTypeDropdownExpanded,
                onExpandedChange = { onFileTypeDropdownExpandedChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = fileType,
                    onValueChange = { onFileTypeSelected(it) },
                    readOnly = true,
                    label = { Text("Select Transaction") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = fileTypeDropdownExpanded)
                    },
                    isError = fileTypeError != null,
                    supportingText = {
                        if (fileTypeError != null) {
                            Text(fileTypeError)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = fileTypeDropdownExpanded,
                    onDismissRequest = { onFileTypeDropdownExpandedChange(false) }
                ) {
                    fileTypeOptions.forEach { transaction ->
                        DropdownMenuItem(
                            text = { Text(transaction) },
                            onClick = {
                                onFileTypeSelected(transaction)
                                onFileTypeDropdownExpandedChange(false)
                            }
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = fileName,
                onValueChange = {},
                isError = fileNameError != null,
                label = { Text("Start Date") },
                placeholder = {
                    Text("Please enter File Name")
                },
                supportingText = {
                    if (fileNameError != null) {
                        Text(fileNameError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onSelectFile,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Select File")
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onUploadFile,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Upload")
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
fun UploadFileScreenPreview() {
    UploadFileDesign(
        fileName = "",
        fileNameError = "",
        onSelectFile = {},
        fileType = "File Format",
        fileTypeOptions = listOf("File Format", "csv"),
        fileTypeError = "",
        onFileTypeSelected = {},
        fileTypeDropdownExpanded = false,
        onFileTypeDropdownExpandedChange = {},
        onUploadFile = {},
        isLoading = false
    )
}