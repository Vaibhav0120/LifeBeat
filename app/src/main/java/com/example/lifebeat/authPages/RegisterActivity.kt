package com.example.lifebeat.authPages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lifebeat.MainActivity
import com.example.lifebeat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // Import Firestore
//import com.google.android.gms.auth.api.signin.GoogleSignIn (Commented out for now)
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInClient (Commented out for now)
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore // Declare Firestore instance
    //private lateinit var googleSignInClient: GoogleSignInClient (Commented out for now)

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginRedirectText: TextView // TextView to redirect to login page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore

        // Initialize Google Sign-In
        /*
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Set your web client ID from Firebase Console
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google sign-in
        findViewById<ImageView>(R.id.google).setOnClickListener {
            signInWithGoogle()
        }

        // Facebook sign-in (Commented out for now)
        findViewById<ImageView>(R.id.fb).setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(this@RegisterActivity, "Facebook Sign-In Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@RegisterActivity, "Facebook Sign-In Error: ${error?.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        */

        // Apple ID sign-in (Commented out for now)
        /*
        findViewById<ImageView>(R.id.apple).setOnClickListener {
            signInWithApple()
        }
        */

        // Initialize views
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.Email)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.chk_Password)
        registerButton = findViewById(R.id.loginButton)
        loginRedirectText = findViewById(R.id.registerToLogin) // Link to login

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Register Button click listener
        registerButton.setOnClickListener {
            registerUser()
        }

        // Set listener to navigate to LoginActivity
        loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close RegisterActivity
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
                        "Email" to email,
                        "isGuest" to false, // Set isGuest to false
                        "HealthInfo" to false // Set HealthInfo to false
                    )

                    // Store user details in Firestore
                    userId?.let {
                        firestore.collection("User").document(it).set(user)
                    }

                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close RegisterActivity
                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Google Sign-In (Commented out for now)
    /*
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign-in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    */

    // Apple Sign-In (Commented out for now)
    /*
    private fun signInWithApple() {
        val provider = OAuthProvider.newBuilder("apple.com")
        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener { authResult ->
                // Successfully signed in
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Apple Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    */

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "RegisterActivity"
    }
}
