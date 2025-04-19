package com.example.managerapp.ui.composescreens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.managerapp.models.Item
import com.example.managerapp.models.TopBarActions
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel


@Composable
fun UploadFileScreen(
    viewModel: ManagerViewModel,
    setTopBarActions: (TopBarActions) -> Unit,
) {

    var fileName by rememberSaveable { mutableStateOf("") }
    var fileType by rememberSaveable { mutableStateOf("File Format") }
    var fileNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var fileTypeError by rememberSaveable { mutableStateOf<String?>(null) }
    var fileTypeDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var csvUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val user by viewModel.currentUser.collectAsState()
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current


    fun processCSV(context: Context, uri: Uri) {
        val contentResolver = context.contentResolver

        try {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.bufferedReader()?.use { reader ->
                reader.readLine()
                reader.forEachLine { line ->
                    val tokens = line.split(",")
                    if (tokens.size >= 4) {
                        val item = Item(
                            null,
                            item_name = tokens[0],
                            item_stock = tokens[1].toIntOrNull() ?: 0,
                            item_cost = tokens[2].toIntOrNull() ?: 0,
                            min_quantity = tokens[3].toIntOrNull() ?: 0
                        )
                        user?.let {
                            viewModel.addItem(
                                it.uid,
                                item
                            )
                        }
                    }
                }
            }
            Toast.makeText(context, "CSV Processed Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            println("error reading csv $e")
            Toast.makeText(context, "Error reading CSV", Toast.LENGTH_SHORT).show()
        }
    }

    fun getFileNameFromUri(uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }

        return uri.lastPathSegment ?: ""
    }


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                // Validate file extension
                val fileExtension = uri.toString().substringAfterLast(".")

                // Method 2: Get MIME type from content resolver
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri)

                // Method 3: Check file name
                val uploadedFileName = getFileNameFromUri(uri)
                val fileNameExtension = uploadedFileName.substringAfterLast(".").lowercase()

                // Comprehensive CSV validation
                val isCsvFile =
                    (fileExtension == "csv" || fileNameExtension == "csv" || mimeType == "text/csv" || mimeType == "application/csv")


                if (isCsvFile) {
                    csvUri = uri
                    fileName = getFileNameFromUri(uri)
                    fileNameError = null
                    fileTypeError = null
                } else {
                    println("file extension = $uri")
                    Toast.makeText(
                        context,
                        "Please select a CSV file $fileExtension",
                        Toast.LENGTH_SHORT
                    ).show()
                    fileNameError = "Invalid file type"
                }
            }
        }
    )

    fun onSelectFile() {
        fileTypeError = if (fileType != "csv")
            "Please select file format"
        else
            null

        if (fileTypeError == null) {
            filePickerLauncher.launch("*/*")
        }
    }

    fun clearFields() {
        fileType = "File Format"
        fileName = ""
        fileNameError = null
        fileTypeError = null
        csvUri = null
    }

    fun onUploadFile() {
        fileTypeError = if (fileType != "csv")
            "Please select file format"
        else
            null

        fileNameError = if (fileName == "")
            "Please select your file"
        else
            null

        if (fileTypeError == null && fileNameError == null) {
            csvUri?.let { processCSV(context, it) }
        }

    }

    LaunchedEffect(Unit) {
        setTopBarActions(
            TopBarActions(
                onClear = { clearFields() }
            )
        )
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
                    isLoading = false
                    clearFields()
                }
            }

        }
    }

    UploadFileDesign(
        fileName = fileName,
        fileNameError = fileNameError,
        onSelectFile = { onSelectFile() },
        fileType = fileType,
        fileTypeOptions = listOf("File Format", "csv"),
        fileTypeError = fileTypeError,
        onFileTypeSelected = {
            fileType = it
        },
        fileTypeDropdownExpanded = fileTypeDropdownExpanded,
        onFileTypeDropdownExpandedChange = {
            fileTypeDropdownExpanded = it
        },
        onUploadFile = { onUploadFile() },
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
                readOnly = true,
                isError = fileNameError != null,
                label = { Text("File Name") },
                placeholder = {
                    Text("Please select your file")
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