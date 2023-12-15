package com.example.adminfoodgasm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.adminfoodgasm.adapter.OrdersListAdapter
import com.example.adminfoodgasm.databinding.ActivityOrderListBinding
import com.example.adminfoodgasm.viewmodel.OrderListViewModel
import kotlinx.coroutines.launch

class OrderListActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOrderListBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[OrderListViewModel::class.java]
    }
    private val ordersAdapter by lazy { OrdersListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.orders.observe(this) {
            ordersAdapter.differ.submitList(it)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllOrders()
            }
        }
    }

    private fun setupViews() {
        binding.rvOrders.apply {
            setHasFixedSize(true)
            adapter = ordersAdapter
        }
        ordersAdapter.onClick = { order ->
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("args", order)
            startActivity(intent)
        }
    }
}