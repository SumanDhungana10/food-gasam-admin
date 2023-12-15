package com.example.adminfoodgasm


import android.annotation.SuppressLint
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
import com.example.adminfoodgasm.model.RestaurantModel
import com.example.adminfoodgasm.adapter.RestaurantAdapter
import com.example.adminfoodgasm.databinding.ActivityRestaurantListBinding
import com.example.adminfoodgasm.databinding.DialogAddRestaurantBinding
import com.example.adminfoodgasm.utils.fixLayout
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RestaurantListActivity : AppCompatActivity() {
    private val activityBinding by lazy {
        ActivityRestaurantListBinding.inflate(layoutInflater)
    }
    private val dialogBinding by lazy {
        DialogAddRestaurantBinding.inflate(layoutInflater)
    }
    private lateinit var arrayList: ArrayList<RestaurantModel>
    lateinit var db: FirebaseFirestore
    lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBinding.root)
        activityBinding.progressBar.visibility = View.INVISIBLE
        supportActionBar?.hide()
        initialize()
        handleEvents()
        recyclerView()


    }

    private fun recyclerView() {
        arrayList = arrayListOf()
        val adapter = RestaurantAdapter(this@RestaurantListActivity, arrayList)
        activityBinding.restaurantrv.adapter = adapter
        activityBinding.restaurantrv.layoutManager =
            LinearLayoutManager(this@RestaurantListActivity)

        db = FirebaseFirestore.getInstance()
        db.collection("Restaurant").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val restaurant = dc.document.toObject(RestaurantModel::class.java)
                        arrayList.add(restaurant)
                    }
                }
                adapter.notifyDataSetChanged()
            }

        })

    }

    private fun handleEvents() {
        activityBinding.add.setOnClickListener {
            val addDialog = Dialog(this)
            val contentView = dialogBinding.root

            dialogBinding.name.text.clear()
            dialogBinding.imageView.setImageDrawable(null)
            dialogBinding.address.text.clear()
            dialogBinding.discount.text.clear()
            dialogBinding.duration.text.clear()

            if (contentView.parent != null) {
                (contentView.parent as? ViewGroup)?.removeView(contentView)
            }
            addDialog.setContentView(contentView)
            dialogBinding.addimage.setOnClickListener {
                selectImage()
            }
            dialogBinding.submit.setOnClickListener {
                activityBinding.progressBar.visibility = View.VISIBLE
                activityBinding.mainView.setBackgroundColor(getColor(R.color.g_gray500))
                uploadImage(dialogBinding)
                addDialog.dismiss()
            }
            addDialog.show()
            addDialog.fixLayout()
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

    @SuppressLint("SuspiciousIndentation")
    private fun uploadImage(binding1: DialogAddRestaurantBinding) {
        val id = db.collection("Restaurant").document().id

        val name = binding1.name.text.toString()
        val category = binding1.category.text.toString()
        val address = binding1.address.text.toString()
        val duration = binding1.duration.text.toString()
        val discount = binding1.discount.text.toString()

        if (name.isNotEmpty() && address.isNotEmpty() && duration.isNotEmpty() && imageUri != null) {
            storageReference = storageReference.child(System.currentTimeMillis().toString())
            imageUri?.let { storageReference.putFile(it) }
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val Restaurant: MutableMap<String, Any> = HashMap()
                            Restaurant["id"] = id
                            Restaurant["name"] = name
                            Restaurant["category"] = category
                            Restaurant["address"] = address
                            Restaurant["duration"] = duration
                            Restaurant["discount"] = discount
                            Restaurant["image"] = uri.toString()
                            db.collection("Restaurant").document(id).set(Restaurant)
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