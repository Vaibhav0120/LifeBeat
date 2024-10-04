package com.example.lifebeat.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lifebeat.authPages.LandingPage
import com.example.lifebeat.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Set up the logout button
        val logoutButton: Button = binding.logoutButton
        logoutButton.setOnClickListener {
            logoutUser()
        }

        return binding.root
    }

    private fun logoutUser() {
        val user = auth.currentUser
        user?.let {
            // Check if the user is a guest
            val uid = it.uid
            val guestUserDoc = firestore.collection("User").document(uid)

            guestUserDoc.get().addOnSuccessListener { document ->
                if (document.exists() && document.getBoolean("isGuest") == true) {
                    // Delete user from Firestore
                    guestUserDoc.delete().addOnSuccessListener {
                        // Delete user from Firebase Authentication
                        auth.currentUser?.delete()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Navigate to LandingPage
                                val intent = Intent(requireActivity(), LandingPage::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete guest user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener { e: Exception ->
                        Toast.makeText(requireContext(), "Failed to delete guest user from Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Regular user logout
                    auth.signOut()
                    val intent = Intent(requireActivity(), LandingPage::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "No user logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
