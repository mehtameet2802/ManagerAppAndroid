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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managerapp.R
import com.example.managerapp.adapters.ItemAdapter
import com.example.managerapp.adapters.OnItemInteractionListener
import com.example.managerapp.databinding.FragmentHomeBinding
import com.example.managerapp.models.Item
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnItemInteractionListener {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: ManagerViewModel
    private lateinit var rvAdapter: ItemAdapter
    private lateinit var user:FirebaseUser
    private lateinit var items:List<Item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ManagerActivity).viewModel

        val btnDownload = requireActivity().findViewById<ImageButton>(R.id.customIcon)

        items = emptyList()
        rvAdapter = ItemAdapter(items,this)
        binding.rvHome.layoutManager = LinearLayoutManager(context)
        binding.rvHome.adapter = rvAdapter

        user = viewModel.getCurrentUser()!!

        btnDownload.setOnClickListener {
            if(items.isEmpty())
                Toast.makeText(requireActivity(),"No items found",Toast.LENGTH_LONG).show()
            else{
                Toast.makeText(requireActivity(),"Downloading File",Toast.LENGTH_LONG).show()
                viewModel.generateInventoryPdf("Current Inventory",items,"CurrentInventory")
            }
        }

        observeItemsResult()
    }

    override fun onUpdateStock(item: Item, newStock:Int) {

        viewModel.updateItemStock(user.uid,item.item_id!!,newStock)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateItemStockResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("update stock error ========= ")
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
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
//                            Toast.makeText(requireContext(),"Stock Updated", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }
        }
    }


    private fun observeItemsResult() {
        viewModel.getAllItems(user.uid)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getItemResult.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            println("recyclerview data collection failed error ========= " + resource.data)
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
                            println("recyclerview data collection success ========= " + resource.data)
                            binding.progressBar.visibility = View.GONE
                            items = resource.data!!
                            rvAdapter.updateItems(items)
                            calculateSum(resource.data)
                        }
                    }

                }
            }
        }
    }

    private fun calculateSum(items: List<Item>?) {
        var sum: Int = 0
        if (items != null) {
            for (item in items) {
                sum += item.item_stock!! * item.item_cost!!
            }
        }
        binding.tvAmount.text = sum.toString()
    }
}