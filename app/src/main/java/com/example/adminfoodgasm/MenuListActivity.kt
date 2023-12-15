package com.example.adminfoodgasm

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodgasm.adapter.MenuAdapter
import com.example.adminfoodgasm.databinding.ActivityMenuListBinding
import com.example.adminfoodgasm.databinding.DialogAddMenuBinding
import com.example.adminfoodgasm.model.Menumodel
import com.example.adminfoodgasm.utils.fixLayout
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MenuListActivity : AppCompatActivity() {
    private lateinit var list: ArrayList<Menumodel>
    private lateinit var activityBinding: ActivityMenuListBinding
    private lateinit var id: String
    private val dialogBinding by lazy {
        DialogAddMenuBinding.inflate(layoutInflater)
    }
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMenuListBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        activityBinding.progressBar.visibility = View.INVISIBLE
        db = FirebaseFirestore.getInstance()
        val name = intent.getStringExtra("name")
        id = intent.getStringExtra("id").toString()
        activityBinding.menuheading.text = name.toString()
        handleEvents()
        recyclerView()

    }

    private fun recyclerView() {
        list = arrayListOf()
        val adapter = MenuAdapter(this, list, id!!)
        activityBinding.menurv.adapter = adapter
        activityBinding.menurv.layoutManager = LinearLayoutManager(this)
        db.collection("Restaurant").document(id!!).collection("Menu")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(this@MenuListActivity, "error occured", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                list.add(dc.document.toObject(Menumodel::class.java))
                            }
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
            if (contentView.parent != null) {
                (contentView.parent as? ViewGroup)?.removeView(contentView)
            }
            addDialog.setContentView(contentView)
            dialogBinding.submit.setOnClickListener {
                activityBinding.progressBar.visibility = View.VISIBLE
                uploadInfo()
                addDialog.dismiss()
            }
            addDialog.show()
            addDialog.fixLayout()
        }
    }

    private fun uploadInfo() {
        val menuid = db.collection("Restaurant").document().id
        val name = dialogBinding.name.text.toString()

        val Menu: MutableMap<String, Any> = HashMap()
        Menu["id"] = menuid
        Menu["name"] = name
        db.collection("Restaurant").document(id!!).collection("Menu").document(menuid).set(Menu)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Menu added Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
                activityBinding.progressBar.visibility = View.INVISIBLE
            }
    }
}