package com.example.adminfoodgasm.model

sealed class OrderStatus(val status: String) {
    data object Ordered : OrderStatus("Ordered")
    data object Canceled : OrderStatus("Canceled")
    data object Confirmed : OrderStatus("Confirmed")
    data object Shipped : OrderStatus("Shipped")
    data object Delivered : OrderStatus("Delivered")
    data object Returned : OrderStatus("Returned")
}

private val pendingStatuses = listOf(
    OrderStatus.Ordered,
    OrderStatus.Confirmed,
    OrderStatus.Shipped
)

fun isPending(status: OrderStatus) = pendingStatuses.any { status == it }
fun isCompleted(status: OrderStatus) = OrderStatus.Delivered == status

fun findOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> OrderStatus.Ordered
        "Canceled" -> OrderStatus.Canceled
        "Confirmed" -> OrderStatus.Confirmed
        "Shipped" -> OrderStatus.Shipped
        "Delivered" -> OrderStatus.Delivered
        else -> OrderStatus.Returned
    }
}
