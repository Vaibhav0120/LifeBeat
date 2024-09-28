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
import com.example.lifebeat.R
import com.example.lifebeat.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // Import Firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore // Declare Firestore instance

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore

        // Initialize views
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.Email)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.chk_Password)
        registerButton = findViewById(R.id.loginButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username = usernameEditText.text.toString().trim() // Get the username
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User registration successful
                    val userId = auth.currentUser?.uid // Get UID of the registered user

                    // Create a user object
                    val user = hashMapOf(
                        "Username" to username,
                        "Email" to email
                    )

                    // Store user details in Firestore
                    userId?.let {
                        firestore.collection("User").document(it).set(user)
                    }

                    // Navigate to MainActivity
                    val intent = Intent(this, com.example.lifebeat.MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close RegisterActivity
                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
