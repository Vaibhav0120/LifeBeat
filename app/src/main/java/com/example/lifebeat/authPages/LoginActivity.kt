package com.example.lifebeat.authPages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifebeat.MainActivity
import com.example.lifebeat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val usernameOrEmail = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validate input
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the input is an email or a username and retrieve email if it's a username
        if (isEmail(usernameOrEmail)) {
            // If it is an email, authenticate with email
            authenticateWithEmail(usernameOrEmail, password)
        } else {
            // If it is a username, query Firestore to find the email associated with the username
            authenticateWithUsername(usernameOrEmail, password)
        }
    }

    private fun authenticateWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in success
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close LoginActivity
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun authenticateWithUsername(username: String, password: String) {
        // Query Firestore to find the email associated with the username
        firestore.collection("User")
            .whereEqualTo("Name", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No user found with that username", Toast.LENGTH_SHORT).show()
                } else {
                    // Get the email from the document
                    val email = documents.documents[0].getString("Email")
                    if (email != null) {
                        authenticateWithEmail(email, password)
                    } else {
                        Toast.makeText(this, "Email not found for this username", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error retrieving user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isEmail(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }
}
