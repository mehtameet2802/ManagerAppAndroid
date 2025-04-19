package com.example.managerapp.ui.composescreens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.managerapp.models.TopBarActions
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.ManagerViewModel

//
//@AndroidEntryPoint
//class InventoryStatusFragment : Fragment(), OnItemInteractionListener {
//
//    lateinit var binding: FragmentInventoryStatusBinding
//    lateinit var viewModel: ManagerViewModel
//    private lateinit var rvAdapter: ItemAdapter
//    private lateinit var user: FirebaseUser
//    private lateinit var items: List<Item>
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        binding = FragmentInventoryStatusBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel = (activity as ManagerActivity).viewModel
//
//        val btnDownload = requireActivity().findViewById<ImageButton>(R.id.customIcon)
//
//        items = emptyList()
//        rvAdapter = ItemAdapter(items, this)
//        binding.rvInventory.layoutManager = LinearLayoutManager(context)
//        binding.rvInventory.adapter = rvAdapter
//
//        user = viewModel.getCurrentUser()!!
//
//        btnDownload.setOnClickListener {
//            if (items.isEmpty())
//                Toast.makeText(requireActivity(), "No items found", Toast.LENGTH_LONG).show()
//            else {
//                Toast.makeText(requireActivity(), "Downloading File", Toast.LENGTH_LONG).show()
//                viewModel.generateInventoryPdf("Low Inventory", items, "LowInventory")
//            }
//        }
//
//        observeItemsResult()
//    }
//
//    override fun onUpdateStock(item: Item, newStock: Int) {
//        viewModel.updateItemStock(user.uid, item.item_id!!, newStock)
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.updateItemStockResult.collect { resource ->
//                    when (resource) {
//                        is Resource.Error -> {
//                            binding.progressBar.visibility = View.GONE
//                            println("update stock error ========= ")
//                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG)
//                                .show()
//                        }
//
//                        is Resource.Loading -> {
//                            binding.progressBar.visibility = View.VISIBLE
//                        }
//
//                        is Resource.StandBy -> {
//                            binding.progressBar.visibility = View.INVISIBLE
//                        }
//
//                        is Resource.Success -> {
//                            println("stock updated successfully ========= ")
//                            binding.progressBar.visibility = View.GONE
////                            Toast.makeText(requireContext(),"Stock Updated", Toast.LENGTH_LONG).show()
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//
//    private fun observeItemsResult() {
//        viewModel.getAllItems(user.uid)
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.getItemResult.collect { resource ->
//                    when (resource) {
//                        is Resource.Error -> {
//                            binding.progressBar.visibility = View.GONE
//                            println("recyclerview data collection failed error ========= " + resource.data)
//                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG)
//                                .show()
//                        }
//
//                        is Resource.Loading -> {
//                            binding.progressBar.visibility = View.VISIBLE
//                        }
//
//                        is Resource.StandBy -> {
//                            binding.progressBar.visibility = View.INVISIBLE
//                        }
//
//                        is Resource.Success -> {
//                            println("recyclerview data collection success ========= " + resource.data)
//                            binding.progressBar.visibility = View.GONE
//                            val filteredItems = resource.data!!.filter { item ->
//                                item.item_stock!! < item.min_quantity!!
//                            }
//                            items = filteredItems
//                            rvAdapter.updateItems(items)
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//
//}

@Composable
fun InventoryStatusScreen(
    viewModel: ManagerViewModel,
    setTopBarActions: (TopBarActions) -> Unit,
) {

    var isLoading by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(viewModel.updateItemStockResult) {
        viewModel.updateItemStockResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("update stock error ========= ")
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("stock updated successfully ========= ")
                    isLoading = false
//                    Toast.makeText(context,"Stock Updated", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    LaunchedEffect(viewModel.getItemResult) {
        viewModel.getItemResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("recyclerview data collection failed error ========= " + resource.data)
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.StandBy -> isLoading = false

                is Resource.Success -> {
                    println("recyclerview data collection success ========= " + resource.data)
                    isLoading = false
                    val filteredItems = resource.data!!.filter { item ->
                        item.item_stock!! < item.min_quantity!!
                    }
//                    items = filteredItems
//                    rvAdapter.updateItems(items)
                }
            }

        }
    }

    InventoryStatusDesign()

}

@Composable
fun InventoryStatusDesign() {

}

@Preview(showBackground = true)
@Composable
fun InventoryStatusScreenPreview() {
    InventoryStatusDesign()
}