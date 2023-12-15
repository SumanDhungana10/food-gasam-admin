package com.example.adminfoodgasm

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.adminfoodgasm.adapter.BillingProductsAdapter
import com.example.adminfoodgasm.databinding.ActivityOrderDetailBinding
import com.example.adminfoodgasm.model.OrderModel
import com.example.adminfoodgasm.model.OrderStatus
import com.example.adminfoodgasm.model.findOrderStatus
import com.example.adminfoodgasm.viewmodel.OrderDetailViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOrderDetailBinding.inflate(layoutInflater)
    }
    private val billingProductsAdapter by lazy {
        BillingProductsAdapter()
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[OrderDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        val order = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("args", OrderModel::class.java)
        } else {
            intent.getParcelableExtra("args")
        }

        binding.apply {

            tvOrderId.text = "Order #${order?.orderId}"


            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            order?.orderStatus?.let {
                val currentOrderState = when (findOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> 0
                    is OrderStatus.Confirmed -> 1
                    is OrderStatus.Shipped -> 2
                    is OrderStatus.Delivered -> 3
                    else -> 0
                }

                stepView.setOnStepClickListener { clickedStep ->
                    // prevents reversing the order steps
                    if (clickedStep < currentOrderState) return@setOnStepClickListener
                    val newStatus = when (clickedStep) {
                        0 -> OrderStatus.Ordered
                        1 -> OrderStatus.Confirmed
                        2 -> OrderStatus.Shipped
                        3 -> OrderStatus.Delivered
                        else -> OrderStatus.Canceled
                    }
                    val success =
                        viewModel.updateOrderStatus(order.orderId!!, order.orderBy!!, newStatus)
                    if (success) {
                        stepView.go(clickedStep, true)
                    }
                }

                stepView.go(currentOrderState, true)
                if (currentOrderState == 3) {
                    stepView.done(true)
                }
            }

            tvFullName.text = order?.address?.fullName ?: ""
            tvAddress.text = "${order?.address?.street ?: ""} ${order?.address?.city ?: ""}"
            tvPhoneNumber.text = order?.address?.phone ?: ""

            val totalPrice = order?.totalPrice ?: 0.0
            tvTotalPrice.text = "Rs. $totalPrice"

            tvPaymentInfo.text =
                if (order?.paid == true) "Customer paid using Khalti" else "Customer selected Cash on Delivery option"

            if (order?.paid == false) {
                binding.btnValidate.visibility = View.GONE
            }

            binding.btnValidate.setOnClickListener {
                viewModel.validatePayment(order?.txnToken ?: "", order?.totalPrice?.toLong() ?: 0L)
            }

        }

        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = billingProductsAdapter
        }

        billingProductsAdapter.differ.submitList(order?.products)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.message.collectLatest {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}