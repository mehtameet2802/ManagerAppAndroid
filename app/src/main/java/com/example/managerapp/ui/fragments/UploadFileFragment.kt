package com.example.managerapp.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.managerapp.R
import com.example.managerapp.databinding.FragmentUploadFileBinding
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.common.base.Objects
import java.io.BufferedReader
import java.io.InputStreamReader

class UploadFileFragment : Fragment() {

    lateinit var binding: FragmentUploadFileBinding
    lateinit var viewModel: ManagerViewModel
//    lateinit var pickCSVLauncher: ActivityResultLauncher<Intent>
    var csvData = mutableListOf<List<String>>()
    var fileUri: Uri? = null
    private val STORAGE_PERMISSION_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUploadFileBinding.inflate(inflater, container, false)

//        pickCSVLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                data?.data?.let { uri ->
//                    requireActivity().contentResolver.takePersistableUriPermission(
//                        uri,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
//                    fileUri = uri
//                    readCSVFile(uri)
//                }
//            }
//
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ManagerActivity).viewModel

        setFileDropDownList()

        val clearbtn = requireActivity().findViewById<ImageButton>(R.id.customIcon)

        clearbtn.setOnClickListener {
            clearFields()
        }

//        binding.selectFileBtn.setOnClickListener {
//            if (checkPermissions()) {
//                selectFile()
//            } else {
//                requestPermissions()
//            }
//        }
//
//        binding.uploadBtn.setOnClickListener {
//            uploadFile()
//        }

    }

    private fun setFileDropDownList() {
        val fileFormats = listOf("File Format", "csv")
        val fileArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            fileFormats
        )
        binding.tvFileFormat.setAdapter(fileArrayAdapter)
    }

    private fun clearFields() {
        binding.tvFileFormat.setText("File Format", false)
        binding.tvFileName.setText("")
        fileUri = null
        csvData = mutableListOf()
    }

//    private fun selectFile() {
//        val fileFormat = binding.tvFileFormat
//        if (fileFormat.text.isEmpty() || fileFormat.text.toString() == "File Format") {
//            Toast.makeText(requireContext(), "Please select file format", Toast.LENGTH_LONG).show()
//        } else {
//            openCSVPicker()
//        }
//    }
//
//    private fun uploadFile() {
//        val file = binding.tvFileName
//        val fileFormat = binding.tvFileFormat
//
//        if (fileFormat.text.isEmpty() || fileFormat.text.toString() == "File Format")
//            Toast.makeText(requireContext(), "Please select file format", Toast.LENGTH_LONG).show()
//        else if (file.text.isEmpty()) {
//            Toast.makeText(requireContext(), "Please select a file", Toast.LENGTH_LONG).show()
//        } else {
//            if(isCSVFile(fileUri!!)){
//                if (csvData.isEmpty())
//                    Toast.makeText(requireContext(), "Empty File", Toast.LENGTH_LONG).show()
//                else {
//                    csvData.forEach {
//                        Log.d("CSVData", it.toString()) // You can handle it as per your need
//
//                    }
//                }
//            }
//        }
//    }
//
//    private fun openCSVPicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "*/*"  // Allow all types of files
//            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/csv", "application/vnd.ms-excel"))
//        }
//        pickCSVLauncher.launch(intent)
//    }
//
//    private fun readCSVFile(uri: Uri) {
//        try {
//            val inputStream = requireActivity().contentResolver.openInputStream(uri)
//
//            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
//            cursor?.use {
//                if (it.moveToFirst()) {
//                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                    if (nameIndex != -1) {
//                        binding.tvFileName.setText(it.getString(nameIndex))
//                    }
//                }
//            }
//
//            val reader = BufferedReader(InputStreamReader(inputStream))
//            reader.forEachLine { line ->
//                val row = line.split(",") // Assuming the delimiter is comma
//                csvData.add(row)
//            }
//            reader.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    private fun isCSVFile(uri: Uri): Boolean {
//        val fileName = getFileName(uri)
//        return fileName.endsWith(".csv", ignoreCase = true) ||
//                fileName.endsWith(".xls", ignoreCase = true) ||
//                fileName.endsWith(".xlsx", ignoreCase = true)
//    }
//
//    private fun getFileName(uri: Uri): String {
//        var name = ""
//        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (nameIndex != -1) {
//                    name = it.getString(nameIndex)
//                }
//            }
//        }
//        return name
//    }
//
//    private fun checkPermissions(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Environment.isExternalStorageManager()
//        } else {
//            val result = ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//            result == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    private fun requestPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse("package:${requireContext().packageName}")
//                startActivityForResult(intent, 2296)
//            } catch (e: Exception) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                startActivityForResult(intent, 2296)
//            }
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                STORAGE_PERMISSION_CODE)
//        }
//    }




}