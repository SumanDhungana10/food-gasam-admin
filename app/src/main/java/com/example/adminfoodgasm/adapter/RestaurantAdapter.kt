package com.example.adminfoodgasm.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodgasm.MenuListActivity
import com.example.adminfoodgasm.databinding.ItemRestaurantBinding
import com.example.adminfoodgasm.model.RestaurantModel
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantAdapter(val context: Context, val arrayList: ArrayList<RestaurantModel>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {
    val db = FirebaseFirestore.getInstance()

    inner class RestaurantViewHolder(val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            ItemRestaurantBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
// last update
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.binding.restaurantName.text =
            arrayList[position].name.toString() // assuming 'name' is the property you want to display

        holder.binding.delete.setOnClickListener {
            db.collection("Restaurant").document(arrayList[position].id!!).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                }

            arrayList.removeAt(position)
            notifyItemRemoved(position)
        }
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, MenuListActivity::class.java)
            intent.putExtra("id", arrayList[position].id)
            intent.putExtra("name", arrayList[position].name)
            context.startActivity(intent)
        }
    }
}