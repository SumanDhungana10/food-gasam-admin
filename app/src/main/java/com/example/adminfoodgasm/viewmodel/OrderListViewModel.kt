package com.example.adminfoodgasm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminfoodgasm.model.OrderModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderListViewModel : ViewModel() {
    private val _orders = MutableLiveData<List<OrderModel>>(emptyList())
    val orders: LiveData<List<OrderModel>> = _orders

    suspend fun getAllOrders() {
        val snapshot = Firebase.firestore.collection("orders").get().await()
        _orders.value = snapshot.toObjects<OrderModel>()
    }
}