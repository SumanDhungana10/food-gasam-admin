package com.example.adminfoodgasm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminfoodgasm.model.OrderModel
import com.example.adminfoodgasm.model.findOrderStatus
import com.example.adminfoodgasm.model.isCompleted
import com.example.adminfoodgasm.model.isPending
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardViewModel : ViewModel() {
    private val _pendingOrderCount = MutableLiveData(0)
    val pendingOrderCount: LiveData<Int> = _pendingOrderCount

    private val _completedOrderCount = MutableLiveData(0)
    val completedOrderCount: LiveData<Int> = _completedOrderCount

    private val _totalEarning = MutableLiveData(0)
    val totalEarning: LiveData<Int> = _totalEarning

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            fetchAllOrders()
        }
    }

    private suspend fun fetchAllOrders() {
        _isLoading.value = true
        val snapshot = Firebase.firestore.collection("orders").get().await()
        val orders = snapshot.toObjects<OrderModel>()
        updateState(orders)
        _isLoading.value = false
    }

    private fun updateState(orders: List<OrderModel>) {
        var pendingCount = 0
        var completedCount = 0
        var totalEarning = 0
        orders.forEach { order ->
            order.orderStatus?.let { status ->
                val orderStatus = findOrderStatus(status)
                if (isPending(orderStatus)) pendingCount++
                if (isCompleted(orderStatus)) completedCount++
                totalEarning += order.totalPrice ?: 0
            }
        }
        _pendingOrderCount.value = pendingCount
        _completedOrderCount.value = completedCount
        _totalEarning.value = totalEarning
    }
}