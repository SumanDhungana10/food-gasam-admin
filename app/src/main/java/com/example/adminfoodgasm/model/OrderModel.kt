package com.example.adminfoodgasm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(
    val address: Address? = null,
    val date: String? = null,
    val orderId: Long? = null,
    val orderStatus: String? = null,
    val orderBy: String? = null,
    val products: List<CartProduct>? = null,
    val totalPrice: Int? = null,
    val paid: Boolean = false,
    val txnId: String? = null,
    val txnToken: String? = null
) : Parcelable

@Parcelize
data class Address(
    val addressTitle: String? = null,
    val fullName: String? = null,
    val street: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val state: String? = null,
) : Parcelable

@Parcelize
data class Product(
    val quantity: Int? = null,
    val restaurant: RestaurantModel? = null,
) : Parcelable