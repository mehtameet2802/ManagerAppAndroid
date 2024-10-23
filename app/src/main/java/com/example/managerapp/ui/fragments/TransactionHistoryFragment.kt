package com.example.managerapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.managerapp.databinding.FragmentTransactionHistoryBinding
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseUser
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

        binding.datesBtn.setOnClickListener {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

            picker.show(parentFragmentManager,"TransactionFragment")

            picker.addOnPositiveButtonClickListener {
                val startDate = it.first/1000
                val endDate = it.second/1000
                viewModel.getTransactionHistory(user.uid,startDate.toString(),endDate.toString())
                binding.tvStartDate.setText(convertTimeToDate(it.first))
                binding.tvEndDate.setText(convertTimeToDate(it.second))
                Log.d("HistoryFragment",it.toString())
            }

            picker.addOnNegativeButtonClickListener {
                picker.dismiss()
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