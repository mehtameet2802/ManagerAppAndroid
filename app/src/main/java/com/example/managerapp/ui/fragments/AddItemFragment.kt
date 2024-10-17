package com.example.managerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.managerapp.R
import com.example.managerapp.databinding.FragmentAddItemBinding
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.viewmodel.ManagerViewModel

class AddItemFragment : Fragment() {

    lateinit var binding:FragmentAddItemBinding
    lateinit var viewModel: ManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ManagerActivity).viewModel

        val btnClear = requireActivity().findViewById<ImageButton>(R.id.customIcon)
        val btnSave = binding.saveBtn

        btnClear.setOnClickListener {
            clearFields()
        }

        btnSave.setOnClickListener {
            clearFields()
        }

    }

    private fun saveItem(){

    }

    private fun clearFields() {
        binding.tvItem.setText("")
        binding.tvCost.setText("")
        binding.tvQuantity.setText("")
        binding.tvStock.setText("")
    }

}