package com.example.adminfoodgasm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.adminfoodgasm.databinding.ActivityDashboardBinding
import com.example.adminfoodgasm.viewmodel.DashboardViewModel

class DashboardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[DashboardViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.viewRestaurantCardView.setOnClickListener {
            val intent = Intent(this@DashboardActivity, RestaurantListActivity::class.java)
            startActivity(intent)
        }
        binding.manageOrderStatusCardView.setOnClickListener {
            val intent = Intent(this@DashboardActivity, OrderListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.pendingOrderCount.observe(this) {
            binding.pendingOrderCountTextView.text = (it ?: 0).toString()
        }
        viewModel.completedOrderCount.observe(this) {
            binding.completedOrderCountTextView.text = (it ?: 0).toString()
        }
        viewModel.totalEarning.observe(this) {
            binding.totalEarningAmountTextView.text = "Rs.".plus(it ?: 0)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.pgbarDashboard.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}