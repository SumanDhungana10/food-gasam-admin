package com.example.adminfoodgasm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminfoodgasm.model.OrderStatus
import com.example.adminfoodgasm.model.VerificationPayload
import com.example.adminfoodgasm.utils.NetworkUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderDetailViewModel : ViewModel() {
    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun updateOrderStatus(orderId: Long, orderBy: String, status: OrderStatus): Boolean {
        try {
            viewModelScope.launch {
                Firebase.firestore.runBatch { batch ->
                    // Update the order status in user-orders collection
                    // Update the order into orders collection

                    Firebase.firestore.collection("user")
                        .document(orderBy)
                        .collection("orders")
                        .document(orderId.toString())
                        .update("orderStatus", status.status)

                    Firebase.firestore.collection("orders")
                        .document(orderId.toString())
                        .update("orderStatus", status.status)
                }.await()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun validatePayment(token: String, amount: Long) {
        viewModelScope.launch {
            val response = NetworkUtils.verifyPayment(VerificationPayload(token, amount))
            if (response.isSuccessful) {
                if (response.body()?.amount?.toLong() == amount && response.body()?.state?.name == "Completed") {
                    _message.emit("Payment completed and verified!")
                } else {
                    _message.emit("Payment insufficient or not completed!")
                }
            }else{
                _message.emit("Error occurred: ${response.errorBody()?.string()}")
            }
        }
    }
}