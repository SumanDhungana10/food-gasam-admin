package com.example.adminfoodgasm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodgasm.databinding.BillingProductsRvItemBinding
import com.example.adminfoodgasm.model.CartProduct
import com.example.adminfoodgasm.utils.getProductPrice


//class for billing product
class BillingProductsAdapter :
    RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {

    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(product.image).into(imageCartProduct)
                tvProductCartName.text = product.name
                tvBillingProductQuantity.text = product.quantity.toString()
                tvProductCartPrice.text = "Rs. ${String.format("%.2f", product.getDiscountedProductPrice())}"
            }
        }
    }
    // diffUtil for updating the recycler view
    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    // inflating the view holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }
    // binding the view holder
    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}
//end of billing product adapter