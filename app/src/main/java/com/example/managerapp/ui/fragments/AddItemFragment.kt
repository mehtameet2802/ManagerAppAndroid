package com.example.managerapp.ui.fragments

import android.os.Bundle
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
import com.example.managerapp.databinding.FragmentAddItemBinding
import com.example.managerapp.models.Item
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AddItemFragment : Fragment() {

    lateinit var binding: FragmentAddItemBinding
    lateinit var viewModel: ManagerViewModel
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ManagerActivity).viewModel

        val btnClear = requireActivity().findViewById<ImageButton>(R.id.customIcon)
        val btnSave = binding.saveBtn

        user = viewModel.getCurrentUser()!!

        btnClear.setOnClickListener {
            clearFields()
        }

        btnSave.setOnClickListener {
            saveItem()
        }

        observeAddItemResult()
    }

    private fun saveItem() {
        val itemName = binding.tvItem.text
        val itemStock = binding.tvStock.text
        val itemCost = binding.tvCost.text
        val itemQuantity = binding.tvQuantity.text
        if (itemName.isEmpty())
            binding.tvItem.error = "Enter Item Name"
        else if (itemCost.isEmpty())
            binding.tvCost.error = "Enter Item Cost"
        else if (itemQuantity.isEmpty())
            binding.tvQuantity.error = "Enter min quantity"
        else if (itemStock.isEmpty())
            binding.tvStock.error = "Enter Item Stock"
        else{
            viewModel.addItem(
                user.uid,
                Item(
                    null,
                    itemName.toString(),
                    itemCost.toString().toInt(),
                    itemStock.toString().toInt(),
                    itemQuantity.toString().toInt()
                )
            )
        }
    }

    private fun clearFields() {
        binding.tvItem.setText("")
        binding.tvCost.setText("")
        binding.tvQuantity.setText("")
        binding.tvStock.setText("")
    }

    private fun observeAddItemResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addItemResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("unable to add new item ========= ")
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                        }

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.StandBy -> {
                            binding.progressBar.visibility = View.INVISIBLE
                        }

                        is Resource.Success -> {
                            println("new item addedd successfully ========= " + resource.data)
                            Toast.makeText(requireContext(),"Item Added", Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                            clearFields()
                        }
                    }

                }
            }
        }
    }

}