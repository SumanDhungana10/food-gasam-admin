package com.example.adminfoodgasm

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodgasm.adapter.ItemAdapter
import com.example.adminfoodgasm.databinding.ActivityFoodItemBinding
import com.example.adminfoodgasm.databinding.DialogAddItemBinding
import com.example.adminfoodgasm.model.ItemModel
import com.example.adminfoodgasm.utils.fixLayout
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FoodItemActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityFoodItemBinding
    private lateinit var list: ArrayList<ItemModel>
    lateinit var db: FirebaseFirestore
    lateinit var storageReference: StorageReference
    private val dialogBinding by lazy {
        DialogAddItemBinding.inflate(layoutInflater)
    }
    private lateinit var id: String
    private lateinit var restroId: String
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityFoodItemBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        activityBinding.progressBar.visibility = View.INVISIBLE
        id = intent.getStringExtra("id").toString()
        restroId = intent.getStringExtra("Restroid").toString()
        val name = intent.getStringExtra("name")
        activityBinding.menuheading.text = name

        initialize()
        handleEvents()
        recyclerView()
    }

    private fun recyclerView() {
        list = arrayListOf()
        val adapter = ItemAdapter(this, list, restroId, id)
        activityBinding.itemrv.adapter = adapter
        activityBinding.itemrv.layoutManager = LinearLayoutManager(this)
        db.collection("Restaurant")
            .document(restroId)
            .collection("Menu")
            .document(id)
            .collection("Items")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this@FoodItemActivity, "error occured", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val itemModel = dc.document.toObject(ItemModel::class.java)
                            Log.d(
                                "ItemActivitySal",
                                "Original price value: ${dc.document["price"]}"
                            )
                            Log.d(
                                "ItemActivitySal",
                                "Deserialized price value: ${itemModel.price}"
                            )
                            list.add(dc.document.toObject(ItemModel::class.java))
                        }
                        if (dc.type == DocumentChange.Type.REMOVED) {
                            val itemModel = dc.document.toObject(ItemModel::class.java)
                            Log.d(
                                "ItemActivitySal",
                                "Original price value: ${dc.document["price"]}"
                            )
                            Log.d(
                                "ItemActivitySal",
                                "Deserialized price value: ${itemModel.price}"
                            )
                            list.remove(dc.document.toObject(ItemModel::class.java))
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun handleEvents() {
        activityBinding.add.setOnClickListener {
            val itemDialog = Dialog(this)
            val contentView = dialogBinding.root
            dialogBinding.name.text.clear()
            dialogBinding.price.text.clear()
            dialogBinding.description.text.clear()
            dialogBinding.imageView.setImageDrawable(null)
            if (contentView.parent != null) {
                (contentView.parent as? ViewGroup)?.removeView(contentView)
            }
            itemDialog.setContentView(dialogBinding.root)
            dialogBinding.addimage.setOnClickListener {
                selectImage()
            }
            dialogBinding.submit.setOnClickListener {
                activityBinding.progressBar.visibility = View.VISIBLE
                uploadImage(dialogBinding)
                itemDialog.dismiss()
            }
            itemDialog.show()
            itemDialog.fixLayout()
        }
    }

    fun initialize() {
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("Image")
    }

    private fun selectImage() {
        resultLauncher.launch("image/*")
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        dialogBinding.imageView.setImageURI(it)

    }

    private fun uploadImage(dialogBinding: DialogAddItemBinding) {
        val idd = db.collection("Restaurant").document(restroId).collection("Menu").document().id
        val price = (dialogBinding.price.text.toString()).toFloat()
        val desc = dialogBinding.description.text.toString()
        val name = dialogBinding.name.text.toString()

        if (name.isNotEmpty() && price != 0f && desc.isNotEmpty() && imageUri != null) {
            storageReference = storageReference.child(System.currentTimeMillis().toString())
            imageUri?.let { storageReference.putFile(it) }
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val Restaurant: MutableMap<String, Any> = HashMap()
                            Restaurant["id"] = idd
                            Restaurant["price"] = price
                            Restaurant["desc"] = desc
                            Restaurant["name"] = name
                            Restaurant["image"] = uri.toString()
                            db.collection("Restaurant").document(restroId).collection("Menu")
                                .document(id).collection("Items").document(idd).set(Restaurant)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Successfully added to firestore",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("Success", "Successful")
                                    } else {
                                        Toast.makeText(this, "Firestore failed", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    activityBinding.progressBar.visibility = View.INVISIBLE
                                }
                            imageUri = null
                        }
                    } else {
                        Toast.makeText(this, "Firebase storage Failed", Toast.LENGTH_SHORT).show()
                    }

                }
        } else {
            runOnUiThread {
                activityBinding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "Fill up all the information", Toast.LENGTH_SHORT).show()
            }
        }
    }
}