package com.example.managerapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.managerapp.R
import com.example.managerapp.databinding.FragmentTransactionHistoryBinding
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TransactionHistoryFragment : Fragment() {

    lateinit var binding: FragmentTransactionHistoryBinding
    lateinit var viewModel: ManagerViewModel
    private lateinit var user:FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransactionHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ManagerActivity).viewModel

        user = viewModel.getCurrentUser()!!

        var startDate = ""
        var endDate = ""

        val btnClear = requireActivity().findViewById<ImageButton>(R.id.customIcon)

        btnClear.setOnClickListener {
            startDate = ""
            endDate = ""
            clearFields()
        }

        binding.datesBtn.setOnClickListener {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

            picker.show(parentFragmentManager,"TransactionFragment")

            picker.addOnPositiveButtonClickListener {
                startDate = (it.first/1000).toString()
                endDate = (it.second/1000).toString()

                binding.tvStartDate.setText(convertTimeToDate(it.first))
                binding.tvEndDate.setText(convertTimeToDate(it.second))
                Log.d("HistoryFragment",it.toString())
            }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss()
            }
        }

        binding.downloadBtn.setOnClickListener {
            if(startDate.isEmpty() && endDate.isEmpty())
                Toast.makeText(requireContext(),"Select start and end date",Toast.LENGTH_LONG).show()
            viewModel.getTransactionHistory(user.uid,startDate,endDate)
        }



        observeTransactionHistory()
    }

    private fun clearFields() {
        binding.tvStartDate.setText("")
        binding.tvEndDate.setText("")
    }

    private fun observeTransactionHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTransactionHistoryResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("error getting the transactions ========= ")
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG)
                                .show()
                        }

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.StandBy -> {
                            binding.progressBar.visibility = View.INVISIBLE
                        }

                        is Resource.Success -> {
                            println("got transaction successfully ========= ")
                            binding.progressBar.visibility = View.GONE
                            if(resource.data!!.isEmpty())
                                Toast.makeText(requireContext(),"No Transactions Found",Toast.LENGTH_LONG).show()
                            else
                                Toast.makeText(requireContext(),"Downloading Transaction",Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }
        }
    }

    private fun convertTimeToDate(time: Long):String {
        val ist = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        ist.timeInMillis = time
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return format.format(ist.time)
    }

}