package com.example.managerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.managerapp.databinding.FragmentTransactionHistoryBinding
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.viewmodel.ManagerViewModel

class TransactionHistoryFragment : Fragment() {

    lateinit var binding: FragmentTransactionHistoryBinding
    lateinit var viewModel: ManagerViewModel

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

    }

}