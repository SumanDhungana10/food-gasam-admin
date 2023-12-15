package com.example.adminfoodgasm.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adminfoodgasm.R
import com.example.adminfoodgasm.databinding.ItemOrderBinding
import com.example.adminfoodgasm.model.OrderModel
import com.example.adminfoodgasm.model.OrderStatus
import com.example.adminfoodgasm.model.findOrderStatus

class OrdersListAdapter : Adapter<OrdersListAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: ItemOrderBinding) : ViewHolder(binding.root) {
        fun bind(order: OrderModel) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date

                val status = order.orderStatus ?: return
                val colorDrawable = when (findOrderStatus(status)) {
                    is OrderStatus.Ordered -> getColorDrawable(R.color.g_orange_yellow)
                    is OrderStatus.Confirmed -> getColorDrawable(R.color.g_green)
                    is OrderStatus.Delivered -> getColorDrawable(R.color.g_green)
                    is OrderStatus.Shipped -> getColorDrawable(R.color.g_green)
                    is OrderStatus.Canceled -> getColorDrawable(R.color.g_red)
                    is OrderStatus.Returned -> getColorDrawable(R.color.g_red)
                }
                imageOrderState.setImageDrawable(colorDrawable)
            }
        }

        private fun getColorDrawable(@ColorRes id: Int) =
            ColorDrawable(ResourcesCompat.getColor(itemView.resources, id, itemView.context.theme))
    }


    private val diffUtil = object : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((OrderModel) -> Unit)? = null
}