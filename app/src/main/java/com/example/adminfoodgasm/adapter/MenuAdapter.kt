package com.example.adminfoodgasm.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodgasm.FoodItemActivity
import com.example.adminfoodgasm.databinding.ItemRestaurantBinding
import com.example.adminfoodgasm.model.Menumodel
import com.google.firebase.firestore.FirebaseFirestore

class MenuAdapter(val context: Context, val arrayList: ArrayList<Menumodel>, val id: String) :
    RecyclerView.Adapter<MenuAdapter.MeenuViewHolder>() {
    val db = FirebaseFirestore.getInstance()

    class MeenuViewHolder(val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeenuViewHolder {
        return MeenuViewHolder(
            ItemRestaurantBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MeenuViewHolder, position: Int) {
        holder.binding.restaurantName.text = arrayList[position].name.toString()
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, FoodItemActivity::class.java)
            intent.putExtra("Restroid", id)
            intent.putExtra("id", arrayList[position].id)
            intent.putExtra("name", arrayList[position].name)
            context.startActivity(intent)

        }
        holder.binding.delete.setOnClickListener {
            db.collection("Restaurant").document(id).collection("Menu")
                .document(arrayList[position].id!!)
                .delete().addOnSuccessListener {
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