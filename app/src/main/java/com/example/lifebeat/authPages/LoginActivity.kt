package com.example.lifebeat.authPages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var loginToRegister: TextView
    private lateinit var guestLogin: TextView // Added for guest login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        loginToRegister = findViewById(R.id.loginToRegister)
        guestLogin = findViewById(R.id.GuestLogin) // Initialize guest login TextView

        loginToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        guestLogin.setOnClickListener {
            loginAsGuest() // Call guest login method when clicked
        }

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

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEmail(usernameOrEmail)) {
            authenticateWithEmail(usernameOrEmail, password)
        } else {
            authenticateWithUsername(usernameOrEmail, password)
        }
    }

    private fun authenticateWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun authenticateWithUsername(username: String, password: String) {
        firestore.collection("User")
            .whereEqualTo("Username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No user found with that username", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for (document in documents) {
                    val email = document.getString("Email")
                    if (email != null) {
                        authenticateWithEmail(email, password)
                    } else {
                        Toast.makeText(this, "Email not found for this username", Toast.LENGTH_SHORT).show()
                    }
                    break // Exit loop after the first match
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error retrieving users: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginAsGuest() {
        // Create a new user document in Firestore for the guest
        val guestUsername = "Guest User"
        val guestEmail = "guest_${System.currentTimeMillis()}@example.com" // Unique email for each guest
        auth.createUserWithEmailAndPassword(guestEmail, "password")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Store user details in Firestore
                        val userData = hashMapOf(
                            "Username" to guestUsername,
                            "Email" to guestEmail,
                            "isGuest" to true,
                            "HealthInfo" to false
                        )

                        firestore.collection("User").document(userId).set(userData)
                            .addOnSuccessListener {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to create guest user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Unable to create guest user.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to create guest user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isEmail(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }
}
