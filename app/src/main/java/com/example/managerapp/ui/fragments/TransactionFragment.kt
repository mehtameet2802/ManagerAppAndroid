package com.example.managerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.managerapp.R
import com.example.managerapp.databinding.FragmentTransactionBinding
import com.example.managerapp.models.Item
import com.example.managerapp.models.Transaction
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {

    lateinit var binding: FragmentTransactionBinding
    lateinit var viewModel: ManagerViewModel
    private lateinit var user: FirebaseUser
    private lateinit var items: List<Item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ManagerActivity).viewModel

        user = viewModel.getCurrentUser()!!

        getSetItemDropDownList()
        setTransDropDownList()

        val btnClear = requireActivity().findViewById<ImageButton>(R.id.customIcon)

        btnClear.setOnClickListener {
            clearFields()
        }

        binding.btnTransact.setOnClickListener {
            performTransaction()
        }

        observeItemsResult()
        observeUpdateItemStock()
        observeTransactionResult()
    }

    private fun performTransaction() {
        val item = binding.tvItem
        val transaction = binding.tvTrans
        val quantity = binding.tvQuant

        if (item.text.isEmpty() || item.text.toString()=="Choose Item")
            item.error = "Select an item"
        else if (transaction.text.isEmpty() || transaction.text.toString()=="Transaction"){
            item.error = null
            transaction.error = "Select transaction"
        }
        else if (quantity.text.isEmpty()){
            item.error = null
            transaction.error = null
            quantity.error = "Enter quantity"
        }
        else {
            quantity.error = null
            item.error = null
            transaction.error = null
            val currentItem = items.filter { it.item_name == item.text.toString() }[0]
            var newStock = currentItem.item_stock!!
            if (transaction.text.toString() == "Buy") {
                newStock += quantity.text.toString().toInt()
            } else if (transaction.text.toString() == "Sell") {
                newStock -= quantity.text.toString().toInt()
            }
            else{
                viewModel.updateItemStock(user.uid, currentItem.item_id!!, newStock)
                viewModel.addTransaction(
                    user.uid,
                    Transaction(
                        "",
                        currentItem.item_cost!!,
                        currentItem.item_name!!,
                        transaction.text.toString(),
                        quantity.text.toString().toInt(),
                        newStock,
                        (System.currentTimeMillis() / 1000).toString()
                    )
                )
            }
        }
    }

    private fun getSetItemDropDownList() {
        viewModel.getAllItems(user.uid)
    }

    private fun setItemDropDownList() {
        val itemList = ArrayList<String>()
        itemList.add("Choose Item")
        for (item in items) {
            itemList.add(item.item_name!!)
        }
        val itemArrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, itemList)
        binding.tvItem.setAdapter(itemArrayAdapter)
    }

    private fun setTransDropDownList() {
        val transactions = listOf("Transaction","Buy", "Sell")
        val transArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            transactions
        )
        binding.tvTrans.setAdapter(transArrayAdapter)
    }

    private fun clearFields() {
        binding.tvItem.setText("Choose Item")
        binding.tvTrans.setText("Transaction")
        binding.tvQuant.setText("")
    }

    private fun observeTransactionResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addTransactionResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("update stock error ========= ")
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
                            println("stock updated successfully ========= ")
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Transaction successful",
                                Toast.LENGTH_LONG
                            ).show()
                            clearFields()
                        }
                    }

                }
            }
        }
    }

    private fun observeUpdateItemStock() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateItemStockResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("update stock error ========= ")
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
                            println("stock updated successfully ========= ")
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }

    private fun observeItemsResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getItemResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("unable to get items ========= ")
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
                            println("got items successfully ========= " + resource.data)
                            binding.progressBar.visibility = View.GONE
                            items = resource.data ?: emptyList()
                            setItemDropDownList()
                        }
                    }
                }
            }
        }
    }

}