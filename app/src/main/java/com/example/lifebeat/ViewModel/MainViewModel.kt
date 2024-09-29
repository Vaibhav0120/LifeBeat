package com.example.lifebeat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lifebeat.Domain.CategoryModel
import com.example.lifebeat.Domain.DoctorsModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _doctors = MutableLiveData<MutableList<DoctorsModel>>()

    val category: LiveData<MutableList<CategoryModel>> = _category
    val doctors: LiveData<MutableList<DoctorsModel>> = _doctors

    fun loadCategory() {
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val categoryModel = childSnapshot.getValue(CategoryModel::class.java)
                    if (categoryModel != null) {
                        list.add(categoryModel) // Add category model to list
                    }
                }
                _category.value = list // Update LiveData with the new list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })
    }

    fun loadDoctors() {
        val Ref = firebaseDatabase.getReference("Doctors")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DoctorsModel>()  // Create a mutable list for DoctorsModel

                for (childSnapshot in snapshot.children) {
                    val doctorModel = childSnapshot.getValue(DoctorsModel::class.java)  // Get doctor object

                    if (doctorModel != null) {
                        list.add(doctorModel)  // Add doctor model to list
                    }
                }
                _doctors.value = list  // Update LiveData with the new list of doctors
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here
            }
        })
    }
}
