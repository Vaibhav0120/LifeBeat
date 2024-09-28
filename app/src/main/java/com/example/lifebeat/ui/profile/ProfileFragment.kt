package com.example.lifebeat.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.lifebeat.authPages.LandingPage // Import the LandingPage
import com.example.lifebeat.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth // Declare FirebaseAuth instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up the logout button
        val logoutButton: Button = binding.logoutButton
        logoutButton.setOnClickListener {
            logoutUser()
        }

        return binding.root
    }

    private fun logoutUser() {
        auth.signOut() // Sign out the user

        // Navigate to LandingPage
        val intent = Intent(requireActivity(), LandingPage::class.java)
        startActivity(intent)
        requireActivity().finish() // Close the current activity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
