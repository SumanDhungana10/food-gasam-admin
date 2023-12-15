package com.example.adminfoodgasm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodgasm.databinding.ItemRestaurantBinding
import com.example.adminfoodgasm.model.ItemModel
import com.google.firebase.firestore.FirebaseFirestore

class ItemAdapter(
    val context: Context,
    val arrayList: ArrayList<ItemModel>,
    val restroId: String,
    val idd: String
) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    val db = FirebaseFirestore.getInstance()

    class ItemViewHolder(val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemRestaurantBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = arrayList[position]
        holder.binding.restaurantName.text = item.name.toString()
        holder.binding.delete.setOnClickListener {
            db.collection("Restaurant")
                .document(restroId)
                .collection("Menu")
                .document(idd)
                .collection("Items")
                .document(item.id!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            arrayList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}